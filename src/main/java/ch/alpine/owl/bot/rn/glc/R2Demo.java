// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.bot.r2.R2Bubbles;
import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.flow.EulerIntegrator;
import ch.alpine.owlets.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.reg.BallRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Ramp;

/** functionality is used in tests */
/* package */ enum R2Demo {
  ;
  static final StateIntegrator STATE_INTEGRATOR = new FixedStateIntegrator( //
      EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, Quantity.of(Rational.of(1, 5), "s"), 5);

  static TrajectoryPlanner simpleEmpty() {
    return simple(EmptyPlannerConstraint.INSTANCE);
  }

  static TrajectoryPlanner simpleR2Bubbles() {
    return simple(RegionConstraints.timeInvariant(R2Bubbles.INSTANCE));
  }

  private static TrajectoryPlanner simple(PlannerConstraint plannerConstraint) {
    Tensor stateRoot = Tensors.vector(-2, -2);
    Tensor stateGoal = Tensors.vector(2, 2);
    Scalar radius = RealScalar.of(0.25);
    Tensor eta = Tensors.vector(8, 8);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(36);
    BallRegion ballRegion = new BallRegion(stateGoal, radius);
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), STATE_INTEGRATOR, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(200);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      GlcNode goalNode = optional.orElseThrow(); // <- throws exception if
      Scalar cost = goalNode.costFromRoot();
      Scalar lowerBound = Ramp.FUNCTION.apply(Vector2Norm.between(stateGoal, stateRoot).subtract(radius));
      if (Scalars.lessThan(cost, lowerBound))
        throw new Throw(cost, lowerBound);
    }
    return trajectoryPlanner;
  }

  private static void demo(TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    OwlGui.glc(trajectoryPlanner);
  }

  static void main() {
    demo(simpleEmpty());
    demo(simpleR2Bubbles());
  }
}
