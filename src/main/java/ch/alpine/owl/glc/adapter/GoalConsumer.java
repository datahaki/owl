// code by jph
package ch.alpine.owl.glc.adapter;

import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface GoalConsumer {
  /** @param goal */
  void accept(Tensor goal);
}
