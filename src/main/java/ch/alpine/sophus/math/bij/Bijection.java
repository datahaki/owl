// code by jph
package ch.alpine.sophus.math.bij;

import ch.alpine.tensor.api.TensorUnaryOperator;

/** injective and surjective mapping.
 * 
 * <p>The concatenation of
 * 1) forward and inverse action, and
 * 2) inverse and forward action
 * reduce to the identity. */
public interface Bijection {
  /** @return forward mapping */
  TensorUnaryOperator forward();

  /** @return inverse mapping */
  TensorUnaryOperator inverse();
}
