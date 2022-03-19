// code by jph
package ch.alpine.sophus.demo.bdn;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.ArrayPlotRender;
import ch.alpine.java.ren.ArrayRender;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.PointsRender;
import ch.alpine.javax.swing.SpinnerLabel;
import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.demo.lev.LeversRender;
import ch.alpine.sophus.ext.api.LogWeighting;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.sca.N;

/* package */ abstract class AbstractDeformationDemo extends AbstractScatteredSetWeightingDemo {
  private static final PointsRender POINTS_RENDER_POINTS = //
      new PointsRender(new Color(64, 128, 64, 64), new Color(64, 128, 64, 255));
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  /** for parameterization of geodesic */
  private static final Tensor DOMAIN = Subdivide.of(0.0, 1.0, 10);
  // ---
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final JButton jButton = new JButton("snap");
  private final JToggleButton jToggleTarget = new JToggleButton("target");
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  // ---
  /** in coordinate specific to geodesic display */
  private Tensor movingOrigin;
  private MovingDomain2D movingDomain2D;

  AbstractDeformationDemo(List<ManifoldDisplay> list, List<LogWeighting> array) {
    super(false, list, array);
    spinnerLogWeighting.addSpinnerListener(v -> recompute());
    // ---
    jToggleHeatmap.setSelected(false);
    spinnerRefine.addSpinnerListener(v -> recompute());
    // ---
    {
      spinnerLength.addSpinnerListener(v -> shuffleSnap());
      spinnerLength.setList(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10));
      spinnerLength.setValue(6);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    timerFrame.jToolBar.add(jToggleAxes);
    jToggleTarget.setSelected(true);
    jToggleTarget.setToolTipText("display target");
    timerFrame.jToolBar.add(jToggleTarget);
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
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    movingOrigin = Tensor.of(getControlPointsSe2().map(N.DOUBLE).stream().map(manifoldDisplay::project));
    recompute();
  }

  @Override
  protected final void recompute() {
    System.out.println("recomp");
    movingDomain2D = updateMovingDomain2D(movingOrigin);
  }

  /** @return method to compute mean (for instance approximation instead of exact mean) */
  abstract BiinvariantMean biinvariantMean();

  abstract MovingDomain2D updateMovingDomain2D(Tensor movingOrigin);

  abstract Tensor shapeOrigin();

  @Override // from RenderInterface
  public final synchronized void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor origin = movingDomain2D.origin();
    Tensor target = getGeodesicControlPoints();
    // ---
    {
      ColorDataGradient colorDataGradient = colorDataGradient().deriveWithOpacity(RealScalar.of(0.5));
      new ArrayRender(movingDomain2D.forward(target, biinvariantMean()), colorDataGradient) //
          .render(geometricLayer, graphics);
    }
    boolean isTarget = jToggleTarget.isSelected();
    if (isTarget) { // connect origin and target pairs with lines/geodesics
      Geodesic geodesic = manifoldDisplay.geodesic();
      graphics.setColor(new Color(128, 128, 128, 255));
      graphics.setStroke(STROKE);
      for (int index = 0; index < origin.length(); ++index) {
        ScalarTensorFunction scalarTensorFunction = //
            geodesic.curve(origin.get(index), target.get(index));
        Tensor ms = Tensor.of(DOMAIN.map(scalarTensorFunction).stream() //
            .map(manifoldDisplay::toPoint));
        graphics.draw(geometricLayer.toPath2D(ms));
      }
      graphics.setStroke(new BasicStroke(1));
    }
    POINTS_RENDER_POINTS //
        .show(manifoldDisplay::matrixLift, shapeOrigin(), origin) //
        .render(geometricLayer, graphics);
    LeversRender leversRender = LeversRender.of(manifoldDisplay, isTarget //
        ? getGeodesicControlPoints()
        : origin, null, geometricLayer, graphics);
    if (isTarget)
      leversRender.renderSequence();
    leversRender.renderIndexP(isTarget ? "q" : "p");
    if (jToggleHeatmap.isSelected())
      ArrayPlotRender.rescale(movingDomain2D.arrayReshape_weights(), colorDataGradient(), 3).render(geometricLayer, graphics);
  }
}
