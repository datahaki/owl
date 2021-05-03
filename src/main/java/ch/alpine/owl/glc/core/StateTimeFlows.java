// code by jph
package ch.alpine.owl.glc.core;

import java.util.Collection;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface StateTimeFlows {
  Collection<Tensor> flows(StateTime stateTime);
}
