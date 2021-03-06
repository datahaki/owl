// code by jph
package ch.alpine.sophus.app.avg;

import ch.alpine.sophus.app.sym.SymGeodesic;
import ch.alpine.sophus.app.sym.SymScalar;
import ch.alpine.sophus.flt.ga.GeodesicCenter;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;

/* package */ class GeodesicCenterSplitsDemo extends AbstractKernelSplitsDemo {
  public GeodesicCenterSplitsDemo() {
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
  }

  @Override
  SymScalar symScalar(Tensor vector) {
    if (!Integers.isEven(vector.length()))
      return (SymScalar) GeodesicCenter.of(SymGeodesic.INSTANCE, spinnerKernel.getValue().get()).apply(vector);
    return null;
  }

  public static void main(String[] args) {
    new GeodesicCenterSplitsDemo().setVisible(1000, 600);
  }
}
