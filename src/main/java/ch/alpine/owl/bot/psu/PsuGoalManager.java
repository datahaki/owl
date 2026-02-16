// code by jph
package ch.alpine.owl.bot.psu;

import java.util.List;

import ch.alpine.owl.glc.adapter.GoalAdapter;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.api.TensorMetric;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.sca.Sign;

/* package */ class PsuGoalManager implements MemberQ, CostFunction {
  /** @param coordinateWrap
   * @param center
   * @param radius
   * @return */
  public static GoalInterface of(TensorMetric coordinateWrap, Tensor center, Tensor radius) {
    PsuGoalManager psuGoalManager = new PsuGoalManager(coordinateWrap, center, radius);
    return new GoalAdapter( //
        SimpleTrajectoryRegionQuery.timeInvariant(psuGoalManager), //
        psuGoalManager);
  }

  // ---
  private final TensorMetric coordinateWrap;
  private final Tensor center;
  private final Tensor radius;

  private PsuGoalManager(TensorMetric coordinateWrap, Tensor center, Tensor radius) {
    this.coordinateWrap = coordinateWrap;
    this.center = center;
    this.radius = radius;
  }

  @Override // from CostFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return StateTimeTrajectories.timeIncrement(glcNode, trajectory);
  }

  @Override // from CostFunction
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }

  @Override // from MemberQ
  public boolean test(Tensor x) {
    return Sign.isNegative(coordinateWrap.distance(x, center).subtract(radius));
  }
}
