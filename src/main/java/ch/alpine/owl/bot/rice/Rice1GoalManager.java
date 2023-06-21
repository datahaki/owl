// code by jph
package ch.alpine.owl.bot.rice;

import java.util.List;

import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TimeInvariantRegion;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Ramp;

/* package */ class Rice1GoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  // ---
  private final Tensor center;
  private final Tensor radius;

  public Rice1GoalManager(EllipsoidRegion ellipsoidRegion) {
    super(new TimeInvariantRegion(ellipsoidRegion));
    center = ellipsoidRegion.center();
    radius = ellipsoidRegion.radius();
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    Scalar pc = x.Get(0);
    Scalar pd = center.Get(0);
    Scalar mindist = Ramp.FUNCTION.apply(Abs.between(pc, pd).subtract(radius.get(0)));
    return mindist; // .divide(1 [m/s]), since max velocity == 1 => division is obsolete
  }
}
