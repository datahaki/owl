// code by jph
package ch.alpine.owl.glc.core;

import java.util.List;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** used in combination with a {@link HeuristicFunction}.
 * 
 * candidate implementations include ImageCostFunction */
@FunctionalInterface
public interface CostIncrementFunction {
  /** @param glcNode from which trajectory starts
   * @param trajectory
   * @param flow along which trajectory was computed
   * @return cost of trajectory along flow */
  Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow);
}
