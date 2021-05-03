// code by ynager
package ch.alpine.owl.glc.core;

import java.util.List;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface PlannerConstraint {
  /** parameters as in {@link CostIncrementFunction}
   * 
   * @param glcNode from which trajectory starts
   * @param trajectory
   * @param flow along which trajectory was computed
   * @return true if planner may create a new node at the last {@link StateTime} in given trajectory */
  boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Tensor flow);
}
