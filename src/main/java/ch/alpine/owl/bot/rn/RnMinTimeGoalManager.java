// code by jph and jl
package ch.alpine.owl.bot.rn;

import java.util.Collection;
import java.util.List;

import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TimeInvariantRegion;
import ch.alpine.owl.region.BallRegion;
import ch.alpine.owl.region.RegionWithDistance;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

/** objective is minimum time.
 * 
 * <p>The distance cost function is suitable for entities that are capable
 * and may need to linger in one spot (u == {0, 0}) because in that case
 * the cost == distance traveled evaluates a non-zero, positive value.
 * 
 * <p>The goal region underlying the target area as well as the heuristic is
 * {@link BallRegion}. */
public class RnMinTimeGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  /** @param regionWithDistance
   * @param controls */
  public static GoalInterface create(RegionWithDistance<Tensor> regionWithDistance, Collection<Tensor> controls) {
    return new RnMinTimeGoalManager(regionWithDistance, RnControls.maxSpeed(controls));
  }

  // ---
  private final RegionWithDistance<Tensor> regionWithDistance;
  private final Scalar maxSpeed;

  /** @param regionWithDistance
   * @param maxSpeed positive */
  public RnMinTimeGoalManager(RegionWithDistance<Tensor> regionWithDistance, Scalar maxSpeed) {
    super(new TimeInvariantRegion(regionWithDistance));
    this.regionWithDistance = regionWithDistance;
    this.maxSpeed = Sign.requirePositive(maxSpeed);
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return regionWithDistance.distance(x).divide(maxSpeed);
  }
}
