// code by jph
package ch.alpine.owl.bot.tn;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.glc.adapter.GoalAdapter;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Ramp;

/** goal region is spherical
 * 
 * objective is minimum path length */
/* package */ class TnGoalManager implements Region<Tensor>, CostFunction, Serializable {
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
    return Vector2Norm.between(from.state(), Lists.last(trajectory).state());
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return Ramp.of(tensorMetric.distance(x, center).subtract(radius));
  }

  @Override // from Region
  public boolean test(Tensor tensor) {
    return Scalars.isZero(minCostToGoal(tensor));
  }

  public GoalInterface getGoalInterface() {
    return new GoalAdapter(CatchyTrajectoryRegionQuery.timeInvariant(this), this);
  }
}
