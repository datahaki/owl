// code by jph and jl
package ch.alpine.owl.bot.rn;

import java.util.List;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TimeInvariantRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Sign;

/** objective is minimum path length
 * path length is measured in Euclidean distance
 * 
 * functionality for testing purpose only */
public class RnNoHeuristicCircleGoalManager extends SimpleTrajectoryRegionQuery implements GoalInterface {
  /** constructor creates a spherical region in R^n with given center and radius.
   * distance measure is Euclidean distance.
   * 
   * @param center vector with length == n
   * @param radius positive */
  public RnNoHeuristicCircleGoalManager(Tensor center, Scalar radius) {
    super(new TimeInvariantRegion(new BallRegion(center, Sign.requirePositive(radius))));
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    StateTime from = glcNode.stateTime();
    return Vector2Norm.between(from.state(), Lists.last(trajectory).state());
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }
}
