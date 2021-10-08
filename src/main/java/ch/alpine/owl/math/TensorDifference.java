// code by jph
package ch.alpine.owl.math;

import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface TensorDifference {
  /** @param p
   * @param q
   * @return action to get from p to q, for instance log(p^-1.q), or q-p */
  Tensor difference(Tensor p, Tensor q);
}
