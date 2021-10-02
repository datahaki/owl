// code by ynager
package ch.alpine.owl.glc.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.VectorScalar;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public final class VectorCostGoalAdapter implements GoalInterface, Serializable {
  private final List<CostFunction> costFunctions;
  private final TrajectoryRegionQuery trajectoryRegionQuery;

  /** @param costFunctions
   * @param region
   * @throws Exception if either of the input parameters is null */
  public VectorCostGoalAdapter(List<CostFunction> costFunctions, Region<Tensor> region) {
    this.costFunctions = Objects.requireNonNull(costFunctions);
    this.trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return VectorScalar.of(costFunctions.stream() //
        .map(costFunction -> costFunction.minCostToGoal(x)));
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return VectorScalar.of(costFunctions.stream() //
        .map(costFunction -> costFunction.costIncrement(glcNode, trajectory, flow)));
  }

  @Override // from TrajectoryRegionQuery
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectoryRegionQuery.firstMember(trajectory);
  }

  @Override // from TrajectoryRegionQuery
  public boolean test(StateTime stateTime) {
    return trajectoryRegionQuery.test(stateTime);
  }
}
