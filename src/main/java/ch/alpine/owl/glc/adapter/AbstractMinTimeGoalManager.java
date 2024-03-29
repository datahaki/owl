// code by jph
package ch.alpine.owl.glc.adapter;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** suggested base class for a goal region with cost function based on elapsed time */
public abstract class AbstractMinTimeGoalManager implements Region<Tensor>, CostFunction, Serializable {
  private final Region<Tensor> region;

  protected AbstractMinTimeGoalManager(Region<Tensor> region) {
    this.region = region;
  }

  @Override // from CostFunction
  public final Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from Region
  public final boolean test(Tensor element) {
    return region.test(element);
  }

  public final GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}
