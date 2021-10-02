// code by jph
package ch.alpine.owl.glc.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** class bundles the capabilities of a given
 * cost function and trajectory region query */
public final class GoalAdapter implements GoalInterface, Serializable {
  private final TrajectoryRegionQuery trajectoryRegionQuery;
  private final CostFunction costFunction;

  public GoalAdapter(TrajectoryRegionQuery trajectoryRegionQuery, CostFunction costFunction) {
    this.trajectoryRegionQuery = trajectoryRegionQuery;
    this.costFunction = costFunction;
  }

  @Override // from TrajectoryRegionQuery
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectoryRegionQuery.firstMember(trajectory);
  }

  @Override // from TrajectoryRegionQuery
  public boolean test(StateTime stateTime) {
    return trajectoryRegionQuery.test(stateTime);
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode node, List<StateTime> trajectory, Tensor flow) {
    return costFunction.costIncrement(node, trajectory, flow);
  }

  @Override // from CostFunction
  public Scalar minCostToGoal(Tensor x) {
    return costFunction.minCostToGoal(x);
  }
}
