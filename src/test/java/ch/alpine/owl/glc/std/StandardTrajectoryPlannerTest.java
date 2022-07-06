// code by jph
package ch.alpine.owl.glc.std;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.core.CheckedTrajectoryPlanner;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.HeuristicAssert;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Ramp;

class StandardTrajectoryPlannerTest {
  @Test
  void testSimple() {
    final Tensor stateRoot = Tensors.vector(-2, -2);
    final Tensor stateGoal = Tensors.vector(2, 2);
    final Scalar radius = DoubleScalar.of(0.25);
    // ---
    Tensor eta = Tensors.vector(8, 8);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 5), 5);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(36);
    GoalInterface goalInterface = RnMinDistGoalManager.sperical(stateGoal, radius);
    // ---
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
    assertEquals(trajectoryPlanner.getStateIntegrator(), stateIntegrator);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(200);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      GlcNode goalNode = optional.get(); // <- throws exception if
      Scalar cost = goalNode.costFromRoot();
      // FIXME OWL TEST abs!?
      Scalar lowerBound = Ramp.of(Vector2Norm.of(stateGoal.subtract(stateRoot)).subtract(radius));
      if (Scalars.lessThan(cost, lowerBound))
        throw Throw.of(cost, lowerBound);
    }
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
    assertTrue(glcExpand.getExpandCount() < 100);
  }

  @Test
  void testSimple2() {
    final Tensor stateRoot = Tensors.vector(-2, -2);
    final Tensor stateGoal = Tensors.vector(2, 2);
    final Scalar radius = DoubleScalar.of(0.25);
    // ---
    Tensor eta = Tensors.vector(8, 8);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(36);
    BallRegion ballRegion = new BallRegion(stateGoal, radius);
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    // ---
    TrajectoryPlanner trajectoryPlanner = CheckedTrajectoryPlanner.wrap(new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 5), 5), //
        controls, EmptyPlannerConstraint.INSTANCE, goalInterface));
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(200);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    assertTrue(optional.isPresent());
  }
}
