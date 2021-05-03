// code by jph
package ch.alpine.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.gds.GeodesicDisplays;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.itp.BarycentricMetricInterpolation;
import ch.alpine.sophus.itp.BarycentricRationalInterpolation;
import ch.alpine.sophus.math.var.InversePowerVariogram;
import ch.alpine.sophus.math.win.KnotSpacing;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.TensorUnaryOperator;

/* package */ class BarycentricRationalInterpolationDemo extends ControlPointsDemo {
  private static final int WIDTH = 400;
  private static final int HEIGHT = 300;
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final JToggleButton jToggleLagra = new JToggleButton("Lagr.");
  private final JToggleButton jToggleBasis = new JToggleButton("basis");

  public BarycentricRationalInterpolationDemo() {
    super(true, GeodesicDisplays.METRIC);
    {
      spinnerBeta.setList(Tensors.fromString("{0, 1/4, 1/2, 3/4, 1}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setIndex(0);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
    }
    {
      spinnerDegree.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
      spinnerDegree.setValue(1);
      spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "degree");
    }
    timerFrame.jToolBar.add(jToggleLagra);
    {
      jToggleBasis.setSelected(true);
      timerFrame.jToolBar.add(jToggleBasis);
    }
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 0, 0}, {4, 3, 1}, {5, -1, -2}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    TensorUnaryOperator tensorUnaryOperator = //
        KnotSpacing.centripetal(geodesicDisplay.parametricDistance(), spinnerBeta.getValue());
    Tensor knots = tensorUnaryOperator.apply(control);
    if (1 < control.length()) {
      Tensor domain = Subdivide.of(knots.get(0), Last.of(knots), 25 * control.length());
      BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
      Tensor basis2 = domain.map(jToggleLagra.isSelected() //
          ? BarycentricMetricInterpolation.la(knots, InversePowerVariogram.of(2))
          : BarycentricMetricInterpolation.of(knots, InversePowerVariogram.of(2)));
      try {
        Tensor curve = Tensor.of(basis2.stream().map(weights -> biinvariantMean.mean(control, weights)));
        new PathRender(Color.RED) //
            .setCurve(curve, false) //
            .render(geometricLayer, graphics);
      } catch (Exception exception) {
        System.err.println("no can do");
      }
      Tensor basis1 = domain.map(BarycentricRationalInterpolation.of(knots, spinnerDegree.getValue()));
      try {
        Tensor curve = Tensor.of(basis1.stream().map(weights -> biinvariantMean.mean(control, weights)));
        new PathRender(Color.BLUE) //
            .setCurve(curve, false) //
            .render(geometricLayer, graphics);
      } catch (Exception exception) {
        System.err.println("no can do");
      }
      if (jToggleBasis.isSelected()) {
        {
          VisualSet visualSet = new VisualSet();
          for (Tensor values : Transpose.of(basis2))
            visualSet.add(domain, values);
          JFreeChart jFreeChart = ListPlot.of(visualSet);
          Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
          jFreeChart.draw(graphics, new Rectangle(dimension.width - WIDTH, dimension.height - HEIGHT, WIDTH, HEIGHT));
        }
        {
          VisualSet visualSet = new VisualSet();
          for (Tensor values : Transpose.of(basis1))
            visualSet.add(domain, values);
          JFreeChart jFreeChart = ListPlot.of(visualSet);
          Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
          jFreeChart.draw(graphics, new Rectangle(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
        }
      }
    }
    // ---
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new BarycentricRationalInterpolationDemo().setVisible(1200, 600);
  }
}
