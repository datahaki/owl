// code by jph
package ch.alpine.owl.bot.rice;

import java.util.List;

import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.RadiusXY;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TimeInvariantRegion;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Ramp;

/** Careful: implementation assumes max speed == 1
 * Cost function attains values as minimal distance (not minimal time)! */
/* package */ class Rice2GoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  // ---
  private final Tensor center;
  private final Scalar radius;

  public Rice2GoalManager(EllipsoidRegion ellipsoidRegion) {
    super(new TimeInvariantRegion(ellipsoidRegion));
    center = Extract2D.FUNCTION.apply(ellipsoidRegion.center());
    this.radius = RadiusXY.requireSame(ellipsoidRegion.radius()); // x-y radius have to be equal
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    Scalar minDist = Ramp.of(Vector2Norm.between(Extract2D.FUNCTION.apply(x), center).subtract(radius));
    return minDist; // .divide(1 [m/s]), since max velocity == 1 => division is obsolete
  }
}
