// code by astoll
package ch.alpine.owl.bot.balloon;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owl.glc.adapter.GoalAdapter;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.region.BallRegion;
import ch.alpine.sophis.crv.d2.Extract2D;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

/** heuristic computes minimum time to goal
 * e.g. shortest path to goal (euclidean distance) travelled with maximum speed */
/* package */ class BalloonMinTimeGoalManager implements Region<Tensor>, CostFunction, Serializable {
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
