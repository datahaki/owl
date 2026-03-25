// code by astoll
package ch.alpine.owl.bot.balloon;

import java.util.List;

import ch.alpine.owlets.glc.adapter.GoalAdapter;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.CostFunction;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.crv.d2.Extract2D;
import ch.alpine.sophis.reg.BallRegion;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.sca.Sign;

/** heuristic computes minimum time to goal
 * e.g. shortest path to goal (euclidean distance) travelled with maximum speed */
class BalloonMinTimeGoalManager implements MemberQ, CostFunction {
  // ---
  private final BallRegion ballRegion;
  /** unit of maxSpeed is velocity, e.g. [m/s] */
  private final Scalar maxSpeed;

  /** @param goal for vectors of length 2
   * @param radius
   * @param maxSpeed positive */
  public BalloonMinTimeGoalManager(Tensor goal, Scalar radius, Scalar maxSpeed) {
    ballRegion = new BallRegion(goal, radius);
    this.maxSpeed = Sign.requirePositive(maxSpeed);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return ballRegion.distance(Extract2D.FUNCTION.apply(x)).divide(maxSpeed); // unit [m] / [m/s] simplifies to [s]
  }

  @Override // from Region
  public boolean test(Tensor element) {
    return ballRegion.test(Extract2D.FUNCTION.apply(element));
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory); // unit [s]
  }

  public GoalInterface getGoalInterface() {
    return new GoalAdapter(SimpleTrajectoryRegionQuery.timeInvariant(this), this);
  }
}
