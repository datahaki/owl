// code by jph
package ch.alpine.owl.bot.tn;

import java.util.List;

import ch.alpine.owlets.glc.adapter.GoalAdapter;
import ch.alpine.owlets.glc.core.CostFunction;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Ramp;

/** goal region is spherical
 * 
 * objective is minimum path length */
class TnGoalManager implements MemberQ, CostFunction {
  private final TensorMetric tensorMetric;
  private final Tensor center;
  private final Scalar radius;

  public TnGoalManager(TensorMetric tensorMetric, Tensor center, Scalar radius) {
    this.tensorMetric = tensorMetric;
    this.center = center;
    this.radius = radius;
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode node, List<StateTime> trajectory, Tensor flow) {
    StateTime from = node.stateTime();
    return Vector2Norm.between(from.state(), trajectory.getLast().state());
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return Ramp.FUNCTION.apply(tensorMetric.distance(x, center).subtract(radius));
  }

  @Override // from Region
  public boolean test(Tensor tensor) {
    return Scalars.isZero(minCostToGoal(tensor));
  }

  public GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}
