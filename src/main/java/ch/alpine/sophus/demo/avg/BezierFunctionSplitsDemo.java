// code by jph
package ch.alpine.sophus.demo.avg;

import java.awt.Dimension;

import javax.swing.JSlider;

import ch.alpine.java.ref.gui.ToolbarFieldsEditor;
import ch.alpine.java.win.LookAndFeels;
import ch.alpine.sophus.crv.bezier.BezierFunction;
import ch.alpine.sophus.gds.S2Display;
import ch.alpine.sophus.sym.SymGeodesic;
import ch.alpine.sophus.sym.SymScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;

/** visualization of geodesic average along geodesics */
public class BezierFunctionSplitsDemo extends AbstractSplitsDemo {
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  public BezierFunctionSplitsDemo() {
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
    // ---
    setGeodesicDisplay(S2Display.INSTANCE);
    setControlPointsSe2(Tensors.fromString("{}"));
  }

  @Override // from GeodesicAverageDemo
  SymScalar symScalar(Tensor vector) {
    int n = vector.length();
    if (0 < n) {
      ScalarTensorFunction scalarTensorFunction = BezierFunction.of(SymGeodesic.INSTANCE, vector);
      Scalar parameter = n <= 1 //
          ? RealScalar.ZERO
          : RationalScalar.of(n, n - 1);
      parameter = parameter.multiply(RationalScalar.of(jSlider.getValue(), 1000));
      return (SymScalar) scalarTensorFunction.apply(parameter);
    }
    return null;
  }

  public static void main(String[] args) {
    LookAndFeels.LIGHT.updateUI();
    new BezierFunctionSplitsDemo().setVisible(1000, 600);
  }
}
