// code by jph
package ch.alpine.owl.math;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;

/** helper predicate */
public enum RadiusXY {
  ;
  /** @param vector of the form {value, value, ...}
   * @return value
   * @throws Exception if the first two entries of given vector are not the same */
  public static Scalar requireSame(Tensor vector) {
    Scalar scalar = vector.Get(0);
    if (scalar.equals(vector.get(1)))
      return scalar;
    throw Throw.of(vector);
  }
}
