// code by jph
package ch.alpine.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.itp.BSplineFunction;
import ch.alpine.tensor.itp.BSplineFunctionCyclic;
import ch.alpine.tensor.itp.BSplineFunctionString;

/** use of tensor lib {@link BSplineFunction} */
/* package */ class R2BSplineFunctionDemo extends AbstractCurvatureDemo {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  // ---
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("cyclic");

  public R2BSplineFunctionDemo() {
    super(ManifoldDisplays.R2_ONLY);
    // ---
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(3);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    // ---
    timerFrame.jToolBar.add(jToggleButton);
  }

  @Override
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics); // control points
    RenderQuality.setDefault(graphics);
    Tensor control = getGeodesicControlPoints();
    Tensor refined = Tensors.empty();
    if (0 < control.length()) {
      int degree = spinnerDegree.getValue();
      boolean cyclic = jToggleButton.isSelected();
      ScalarTensorFunction scalarTensorFunction = cyclic //
          ? BSplineFunctionCyclic.of(degree, control)
          : BSplineFunctionString.of(degree, control);
      refined = Subdivide.of(0, cyclic ? control.length() : control.length() - 1, 100) //
          .map(scalarTensorFunction);
      new PathRender(Color.BLUE).setCurve(refined, cyclic).render(geometricLayer, graphics);
    }
    return refined;
  }

  public static void main(String[] args) {
    new R2BSplineFunctionDemo().setVisible(1200, 600);
  }
}
