// code by jph
package ch.alpine.owl.gui.ren;

import ch.alpine.sophus.math.Extract2D;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;

/* package */ enum StaticHelper {
  ;
  private static final Tensor ZEROS = Tensors.vector(0, 0).unmodifiable();

  static Tensor length2(Tensor vector) {
    switch (vector.length()) {
    case 0:
      return ZEROS;
    case 1:
      return Append.of(vector, RealScalar.ZERO);
    case 2:
      return vector;
    default:
      return Extract2D.FUNCTION.apply(vector);
    }
  }
}
