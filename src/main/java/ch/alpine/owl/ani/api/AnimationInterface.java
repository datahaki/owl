// code by jph
package ch.alpine.owl.ani.api;

import ch.alpine.tensor.Scalar;

@FunctionalInterface
public interface AnimationInterface {
  /** @param now measure of real time */
  void integrate(Scalar now);
}
