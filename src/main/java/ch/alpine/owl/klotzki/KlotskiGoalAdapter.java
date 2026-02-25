// code by jph
package ch.alpine.owl.klotzki;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector1Norm;

/* package */ record KlotskiGoalAdapter(KlotskiGoalRegion klotskiGoalRegion, Tensor goal_xy) //
    implements GoalInterface, Serializable {
  /** Example: for Huarong Tensors.vector(0, 4, 2)
   * 
   * @param stone */
  public static KlotskiGoalAdapter of(Tensor stone) {
    return new KlotskiGoalAdapter(new KlotskiGoalRegion(stone), stone.extract(1, 3));
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return Vector1Norm.between(x.get(0).extract(1, 3), goal_xy);
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return RealScalar.ONE;
  }

  @Override // from TrajectoryRegionQuery
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectory.stream().filter(this).findFirst();
  }

  @Override
  public boolean test(StateTime x) {
    return klotskiGoalRegion.test(x.state());
  }
}
