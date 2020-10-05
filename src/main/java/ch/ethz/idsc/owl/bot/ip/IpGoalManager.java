// code by jph
package ch.ethz.idsc.owl.bot.ip;

import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.math.region.BoundedBoxRegion;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// x == [d v a w]
/* package */ class IpGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  private static final long serialVersionUID = -6468920508964855530L;

  public IpGoalManager(Tensor center, Tensor radius) {
    super(new TimeInvariantRegion(BoundedBoxRegion.fromCenterAndRadius(center, radius)));
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
