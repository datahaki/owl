// code by jph
package ch.alpine.owl.bot.tse2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.HeuristicAssert;
import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.owlets.glc.adapter.AbstractMinTimeGoalManager;
import ch.alpine.owlets.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.GlcTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TrajectorySample;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;

class Tse2EntityTest {
  @Test
  void testForward() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical( //
        Tensors.fromString("{10[m], 0[m], 2, 4[m*s^-1]}"), //
        Tensors.fromString("{1[m], 1[m], 1, 4[m*s^-1]}"));
    final Clip v_range = tse2ComboRegion.v_range();
    assertEquals(v_range.min(), Quantity.of(0, "m*s^-1"));
    assertEquals(v_range.max(), Quantity.of(8, "m*s^-1"));
    FlowsInterface flowsInterface = Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.fromString("{-1[m*s^-2], 0[m*s^-2], 1[m*s^-2]}"));
    Collection<Tensor> controls = flowsInterface.getFlows(1);
    AbstractMinTimeGoalManager tse2ForwardMinTimeGoalManager = //
        new Tse2ForwardMinTimeGoalManager(tse2ComboRegion, controls);
    GoalInterface goalInterface = tse2ForwardMinTimeGoalManager.getGoalInterface();
    PlannerConstraint plannerConstraint = EmptyPlannerConstraint.INSTANCE;
    // new Tse2VelocityConstraint(v_range);
    Tensor eta = Tensors.fromString("{7[m^-1], 7[m^-1], 4, 7[s*m^-1]}");
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        new Tse2Integrator(v_range), Tse2StateSpaceModel.INSTANCE, Scalars.fromString("1/10[s]"), 4);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, //
        stateIntegrator, //
        controls, //
        plannerConstraint, //
        goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.fromString("{0[m], 0[m], 0, 0[m*s^-1]}"), Quantity.of(1, "s")));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    glcExpand.getExpandCount();
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    GlcNode glcNode = optional.get();
    List<TrajectorySample> trajectory = GlcTrajectories.detailedTrajectoryTo(stateIntegrator, glcNode);
    assertTrue(20 < trajectory.size());
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
  }

  @SuppressWarnings("unused")
  @Test
  void testGeneral() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical( //
        Tensors.fromString("{10[m], 0[m], 2, 4[m*s^-1]}"), //
        Tensors.fromString("{1[m], 1[m], 1, 4[m*s^-1]}"));
    final Clip v_range = tse2ComboRegion.v_range();
    assertEquals(v_range.min(), Quantity.of(0, "m*s^-1"));
    assertEquals(v_range.max(), Quantity.of(8, "m*s^-1"));
    FlowsInterface flowsInterface = Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.fromString("{-1[m*s^-2], 0[m*s^-2], 1[m*s^-2]}"));
    Collection<Tensor> controls = flowsInterface.getFlows(1);
    AbstractMinTimeGoalManager tse2ForwardMinTimeGoalManager = //
        new Tse2MinTimeGoalManager(tse2ComboRegion, controls, v_range.max());
    GoalInterface goalInterface = tse2ForwardMinTimeGoalManager.getGoalInterface();
    PlannerConstraint plannerConstraint = EmptyPlannerConstraint.INSTANCE;
    // new Tse2VelocityConstraint(v_range);
    Tensor eta = Tensors.fromString("{7[m^-1], 7[m^-1], 4, 7[s*m^-1]}");
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        new Tse2Integrator(v_range), Tse2StateSpaceModel.INSTANCE, Scalars.fromString("1/10[s]"), 4);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, //
        stateIntegrator, //
        controls, //
        plannerConstraint, //
        goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.fromString("{0[m], 0[m], 0, 0[m*s^-1]}"), Quantity.of(1, "s")));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000); // TODO_YN does not find solution even with 10000
    int expandCount = glcExpand.getExpandCount();
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
  }
}
