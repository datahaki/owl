// code by jph
package ch.alpine.sophus.demo.avg;

import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.sophus.flt.ga.GeodesicCenter;
import ch.alpine.sophus.sym.SymGeodesic;
import ch.alpine.sophus.sym.SymScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.win.WindowFunctions;

public class GeodesicCenterSplitsDemo extends AbstractSplitsDemo {
  public WindowFunctions kernel = WindowFunctions.DIRICHLET;

  public GeodesicCenterSplitsDemo() {
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
  }

  @Override
  SymScalar symScalar(Tensor vector) {
    if (!Integers.isEven(vector.length()))
      return (SymScalar) GeodesicCenter.of(SymGeodesic.INSTANCE, kernel.get()).apply(vector);
    return null;
  }

  public static void main(String[] args) {
    new GeodesicCenterSplitsDemo().setVisible(1000, 600);
  }
}
