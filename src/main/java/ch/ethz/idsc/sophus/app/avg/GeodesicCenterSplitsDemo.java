// code by jph
package ch.ethz.idsc.sophus.app.avg;

import ch.ethz.idsc.sophus.app.sym.SymGeodesic;
import ch.ethz.idsc.sophus.app.sym.SymScalar;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class GeodesicCenterSplitsDemo extends KernelSplitsDemo {
  public GeodesicCenterSplitsDemo() {
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
  }

  @Override
  SymScalar symScalar(Tensor vector) {
    if (vector.length() % 2 == 1)
      return (SymScalar) GeodesicCenter.of(SymGeodesic.INSTANCE, spinnerKernel.getValue()).apply(vector);
    return null;
  }

  public static void main(String[] args) {
    new GeodesicCenterSplitsDemo().setVisible(1000, 600);
  }
}
