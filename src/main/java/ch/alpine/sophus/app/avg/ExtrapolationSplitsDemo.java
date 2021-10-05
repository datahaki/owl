// code by jph
package ch.alpine.sophus.app.avg;

import ch.alpine.java.ref.gui.ToolbarFieldsEditor;
import ch.alpine.sophus.app.sym.SymGeodesic;
import ch.alpine.sophus.app.sym.SymScalar;
import ch.alpine.sophus.flt.ga.GeodesicExtrapolation;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.win.WindowFunctions;

/** visualization of geodesic average along geodesics */
public class ExtrapolationSplitsDemo extends AbstractSplitsDemo {
  public WindowFunctions kernel = WindowFunctions.DIRICHLET;

  public ExtrapolationSplitsDemo() {
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
  }

  @Override // from GeodesicAverageDemo
  SymScalar symScalar(Tensor vector) {
    ScalarUnaryOperator window = kernel.get();
    return 0 < vector.length() //
        ? (SymScalar) GeodesicExtrapolation.of(SymGeodesic.INSTANCE, window).apply(vector)
        : null;
  }

  public static void main(String[] args) {
    new ExtrapolationSplitsDemo().setVisible(1000, 600);
  }
}
