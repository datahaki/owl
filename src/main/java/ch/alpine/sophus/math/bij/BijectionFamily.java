// code by jph
package ch.alpine.sophus.math.bij;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** the term "family" conveys the meaning that the bijection
 * depends on a single parameter, for instance time */
public interface BijectionFamily {
  /** for rendering
   * 
   * @param scalar
   * @return */
  TensorUnaryOperator forward(Scalar scalar);

  /** for collision checking
   * 
   * @param scalar
   * @return */
  TensorUnaryOperator inverse(Scalar scalar);
}
