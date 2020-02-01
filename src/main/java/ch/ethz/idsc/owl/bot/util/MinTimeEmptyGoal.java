// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** represents an empty/unreachable goal region.
 * The implementation is useful to explore/search space uniformly.
 * 
 * cost are increments in time */
public enum MinTimeEmptyGoal implements GoalInterface {
  INSTANCE;

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }

  @Override // from TrajectoryRegionQuery
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return Optional.empty();
  }

  @Override // from TrajectoryRegionQuery
  public final boolean isMember(StateTime stateTime) {
    return false;
  }
}
