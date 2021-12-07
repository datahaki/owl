// code by jph
package ch.alpine.sophus.demo.curve;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldSelection;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.ren.PathRender;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.itp.BSplineFunction;
import ch.alpine.tensor.itp.BSplineFunctionCyclic;
import ch.alpine.tensor.itp.BSplineFunctionString;

/** use of tensor lib {@link BSplineFunction} */
public class R2BSplineFunctionDemo extends AbstractCurvatureDemo {
  @FieldInteger
  @FieldSelection(array = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" })
  public Scalar degree = RealScalar.of(3);
  public Boolean cyclic = false;

  public R2BSplineFunctionDemo() {
    super(ManifoldDisplays.R2_ONLY);
    // ---
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
  }

  @Override
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics); // control points
    RenderQuality.setDefault(graphics);
    Tensor control = getGeodesicControlPoints();
    Tensor refined = Tensors.empty();
    if (0 < control.length()) {
      int _degree = degree.number().intValue();
      ScalarTensorFunction scalarTensorFunction = cyclic //
          ? BSplineFunctionCyclic.of(_degree, control)
          : BSplineFunctionString.of(_degree, control);
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
