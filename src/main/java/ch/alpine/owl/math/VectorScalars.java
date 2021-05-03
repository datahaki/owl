// code by jph
package ch.alpine.owl.math;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public enum VectorScalars {
  ;
  /** @param scalar
   * @return unmodifiable
   * @throws Exception if given scalar is not an instance of {@link VectorScalar} */
  public static Tensor vector(Scalar scalar) {
    return ((VectorScalar) scalar).vector();
  }

  public static Scalar at(Scalar scalar, int index) {
    return ((VectorScalar) scalar).at(index);
  }
}
