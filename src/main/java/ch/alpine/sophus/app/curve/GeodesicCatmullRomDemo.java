// code by ob, jph
package ch.alpine.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import javax.swing.JSlider;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.crv.spline.GeodesicCatmullRom;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.Se2Display;
import ch.alpine.sophus.gui.ren.Curvature2DRender;
import ch.alpine.sophus.gui.win.DubinsGenerator;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.math.win.KnotSpacing;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public class GeodesicCatmullRomDemo extends AbstractCurvatureDemo {
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JSlider jSlider = new JSlider(0, 1000, 500);
  private final JSlider jSliderExponent = new JSlider(0, 1000, 500);

  public GeodesicCatmullRomDemo() {
    super(ManifoldDisplays.SE2C_SE2_R2);
    addButtonDubins();
    // ---
    setGeodesicDisplay(Se2Display.INSTANCE);
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20));
    spinnerRefine.setValue(5);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    jSlider.setPreferredSize(new Dimension(300, 28));
    jSlider.setToolTipText("evaluation parameter");
    timerFrame.jToolBar.add(jSlider);
    // ---
    jSliderExponent.setPreferredSize(new Dimension(200, 28));
    jSliderExponent.setToolTipText("centripetal exponent");
    timerFrame.jToolBar.add(jSliderExponent);
    {
      Tensor dubins = Tensors.fromString("{{1, 1, 0}, {1, 2, -1}, {2, 1, 0.5}}");
      setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 0), //
          Tensor.of(dubins.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
    }
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final int levels = spinnerRefine.getValue();
    final Tensor control = getGeodesicControlPoints();
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    if (4 <= control.length()) {
      ManifoldDisplay manifoldDisplay = manifoldDisplay();
      Geodesic geodesicInterface = manifoldDisplay.geodesic();
      Scalar exponent = RationalScalar.of(2 * jSliderExponent.getValue(), jSliderExponent.getMaximum());
      TensorUnaryOperator centripetalKnotSpacing = //
          KnotSpacing.centripetal(manifoldDisplay.parametricDistance(), exponent);
      Tensor knots = centripetalKnotSpacing.apply(control);
      Scalar lo = knots.Get(1);
      Scalar hi = knots.Get(knots.length() - 2);
      hi = DoubleScalar.of(Math.nextDown(hi.number().doubleValue()));
      Clip interval = Clips.interval(lo, hi);
      Scalar parameter = LinearInterpolation.of(Tensors.of(lo, hi)).At(RationalScalar.of(jSlider.getValue(), jSlider.getMaximum()));
      ScalarTensorFunction scalarTensorFunction = GeodesicCatmullRom.of(geodesicInterface, knots, control);
      Tensor refined = Subdivide.increasing(interval, Math.max(1, levels * control.length())).map(scalarTensorFunction);
      {
        Tensor selected = scalarTensorFunction.apply(parameter);
        geometricLayer.pushMatrix(manifoldDisplay.matrixLift(selected));
        Path2D path2d = geometricLayer.toPath2D(manifoldDisplay.shape());
        graphics.setColor(Color.DARK_GRAY);
        graphics.fill(path2d);
        geometricLayer.popMatrix();
      }
      Tensor render = Tensor.of(refined.stream().map(manifoldDisplay::toPoint));
      Curvature2DRender.of(render, false, geometricLayer, graphics);
      return refined;
    }
    return control;
  }

  public static void main(String[] args) {
    new GeodesicCatmullRomDemo().setVisible(1200, 600);
  }
}
