// code by jph
package ch.alpine.owl.rrts.core;

import ch.alpine.sophus.api.Transition;
import ch.alpine.tensor.Scalar;

@FunctionalInterface
public interface TransitionCostFunction {
  /** @param rrtsNode at which transition starts
   * @param transition
   * @return cost of given transition */
  Scalar cost(RrtsNode rrtsNode, Transition transition);
}
