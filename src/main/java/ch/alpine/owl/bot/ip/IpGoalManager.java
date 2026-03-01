// code by jph
package ch.alpine.owl.bot.ip;

import java.util.List;

import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TimeInvariantRegion;
import ch.alpine.sophis.reg.BoxRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

// x == [d v a w]
/* package */ class IpGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  public IpGoalManager(Tensor center, Tensor radius) {
    super(new TimeInvariantRegion(BoxRegion.fromCenterAndRadius(center, radius)));
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }
}
