// code by jph
package ch.alpine.sophus.app.avg;

import ch.alpine.sophus.app.sym.SymGeodesic;
import ch.alpine.sophus.app.sym.SymScalar;
import ch.alpine.sophus.flt.ga.GeodesicExtrapolation;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** visualization of geodesic average along geodesics */
/* package */ class ExtrapolationSplitsDemo extends AbstractKernelSplitsDemo {
  public ExtrapolationSplitsDemo() {
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {2, 2, 1}, {5, 0, 2}}"));
  }

  @Override // from GeodesicAverageDemo
  SymScalar symScalar(Tensor vector) {
    ScalarUnaryOperator window = spinnerKernel.getValue().get();
    return 0 < vector.length() //
        ? (SymScalar) GeodesicExtrapolation.of(SymGeodesic.INSTANCE, window).apply(vector)
        : null;
  }

  public static void main(String[] args) {
    new ExtrapolationSplitsDemo().setVisible(1000, 600);
  }
}
