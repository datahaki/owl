// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.ArrayRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogMetricWeighting;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.N;

/* package */ abstract class AbstractDeformationDemo extends ScatteredSetCoordinateDemo {
  private static final PointsRender POINTS_RENDER_POINTS = //
      new PointsRender(new Color(64, 128, 64, 64), new Color(64, 128, 64, 255));
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final JButton jButton = new JButton("snap");
  private final JToggleButton jToggleAnchor = new JToggleButton("anchor");
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  // ---
  /** in coordinate specific to geodesic display */
  private Tensor movingOrigin;
  private MovingDomain2D movingDomain2D;

  AbstractDeformationDemo(List<GeodesicDisplay> list, LogMetricWeighting[] array) {
    super(false, list, array);
    setMidpointIndicated(false);
    // ---
    spinnerWeighting.addSpinnerListener(v -> recomputeMD2D());
    spinnerRefine.addSpinnerListener(v -> recomputeMD2D());
    // ---
    {
      spinnerLength.addSpinnerListener(v -> shuffleSnap());
      spinnerLength.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10));
      spinnerLength.setValue(6);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    timerFrame.jToolBar.add(jToggleAxes);
    jToggleAnchor.setSelected(true);
    jToggleAnchor.setToolTipText("display anchor");
    timerFrame.jToolBar.add(jToggleAnchor);
    {
      jButton.addActionListener(l -> snap());
      timerFrame.jToolBar.add(jButton);
    }
  }

  abstract Tensor shufflePointsSe2(int n);

  final void shuffleSnap() {
    setControlPointsSe2(shufflePointsSe2(spinnerLength.getValue()));
    snap();
  }

  final void snap() {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    movingOrigin = Tensor.of(getControlPointsSe2().map(N.DOUBLE).stream().map(geodesicDisplay::project));
    recomputeMD2D();
  }

  final void recomputeMD2D() {
    movingDomain2D = updateMovingDomain2D(movingOrigin);
  }

  abstract BiinvariantMean biinvariantMean();

  abstract MovingDomain2D updateMovingDomain2D(Tensor movingOrigin);

  abstract Tensor shapeOrigin();

  @Override // from RenderInterface
  public final synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor origin = movingDomain2D.origin();
    Tensor target = getGeodesicControlPoints();
    Tensor[][] array = movingDomain2D.forward(target, biinvariantMean());
    new ArrayRender(array, colorDataGradient().deriveWithOpacity(RationalScalar.HALF)) //
        .render(geometricLayer, graphics);
    { // connect origin and target pairs with lines/geodesics
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
      graphics.setColor(new Color(128, 128, 128, 255));
      graphics.setStroke(STROKE);
      for (int index = 0; index < origin.length(); ++index) {
        ScalarTensorFunction scalarTensorFunction = //
            geodesicInterface.curve(origin.get(index), target.get(index));
        Tensor domain = Subdivide.of(0, 1, 15);
        Tensor ms = Tensor.of(domain.map(scalarTensorFunction).stream() //
            .map(geodesicDisplay::toPoint));
        graphics.draw(geometricLayer.toPath2D(ms));
      }
      graphics.setStroke(new BasicStroke(1));
    }
    POINTS_RENDER_POINTS //
        .show(geodesicDisplay::matrixLift, shapeOrigin(), origin) //
        .render(geometricLayer, graphics);
    if (jToggleAnchor.isSelected())
      renderControlPoints(geometricLayer, graphics);
    if (jToggleHeatmap.isSelected())
      new ArrayPlotRender(movingDomain2D.weights(), colorDataGradient(), 0, 12, 3).render(geometricLayer, graphics);
  }
}
