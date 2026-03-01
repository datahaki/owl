// code by jph
package ch.alpine.owl.bot.lv;

import java.util.List;

import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TimeInvariantRegion;
import ch.alpine.sophis.reg.EllipsoidRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;

/** the distance used in the ellipsoid is Euclidean.
 * perhaps more suitable for the state space model would be a logarithmic distance */
/* package */ class LvGoalInterface extends SimpleTrajectoryRegionQuery implements GoalInterface {
  /** @param center
   * @param radius
   * @return */
  public static GoalInterface create(Tensor center, Tensor radius) {
    return new LvGoalInterface(new EllipsoidRegion(center, radius));
  }

  // ---
  public LvGoalInterface(EllipsoidRegion ellipsoidRegion) {
    super(new TimeInvariantRegion(ellipsoidRegion));
    VectorQ.requireLength(ellipsoidRegion.center(), 2);
  }

  @Override // from GoalInterface
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from GoalInterface
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }
}
