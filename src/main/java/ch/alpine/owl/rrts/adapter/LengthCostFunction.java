// code by jph
package ch.alpine.owl.rrts.adapter;

import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.TransitionCostFunction;
import ch.alpine.sophus.crv.Transition;
import ch.alpine.tensor.Scalar;

/** TransitionCostFunction that is a function in Transition::length() */
public enum LengthCostFunction implements TransitionCostFunction {
  INSTANCE;

  @Override // from TransitionCostFunction
  public Scalar cost(RrtsNode rrtsNode, Transition transition) {
    return transition.length();
  }
}
