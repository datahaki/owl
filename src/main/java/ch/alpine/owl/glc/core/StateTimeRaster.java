// code by jph
package ch.alpine.owl.glc.core;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeTensorFunction;
import ch.alpine.tensor.Tensor;

/** @see StateTimeTensorFunction */
@FunctionalInterface
public interface StateTimeRaster {
  /** Example: Floor(eta .* represent(state))
   * 
   * @param stateTime
   * @return */
  Tensor convertToKey(StateTime stateTime);
}
