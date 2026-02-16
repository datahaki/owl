// code by jph and jl
package ch.alpine.owl.glc.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class GlcNodeTest {
  @Test
  void testCompare() {
    StateTime state1 = new StateTime(Tensors.vector(3, 0), RealScalar.of(3));
    StateTime state2 = new StateTime(Tensors.vector(3, 0), RealScalar.of(3));
    Scalar cost1 = RealScalar.of(1);
    Scalar cost2 = RealScalar.of(1);
    Scalar heuristic1 = RealScalar.of(0);
    // Scalar heuristic2 = RealScalar.of(0);
    GlcNode test1 = GlcNode.of(null, state1, cost1, heuristic1);
    // GlcNode test2 = GlcNode.of(null, state1, cost1, heuristic1);
    assertTrue(state1.equals(state1));
    assertTrue(state1.equals(state2));
    // reflexiv
    assertFalse(test1.equals(null));
    // Nodes are completely identical
    assertTrue(test1.equals(test1));
    // Symetrie check
    // assertTrue(test1.equals(test2));
    // assertTrue(test2.equals(test1));
    // test2.setMinCostToGoal(heuristic2);
    // Nodes are identically except heuristic
    // assertTrue(test1.equals(test2));
    // Cost is different ==> different node
    @SuppressWarnings("unused")
    GlcNode test3 = GlcNode.of(null, state1, cost2, heuristic1);
    // assertFalse(test1.equals(test3));
    // Nodes are different in state ==> different
    @SuppressWarnings("unused")
    GlcNode test4 = GlcNode.of(null, state2, cost1, heuristic1);
    // assertFalse(test1.equals(test4));
  }

  @Test
  void testMakeRoot() {
    final Tensor stateRoot = Tensors.vector(-2, -2);
    final Tensor stateGoal = Tensors.vector(2, 2);
    final Scalar radius = DoubleScalar.of(0.25);
    // ---
    Tensor eta = Tensors.vector(8, 8);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, Rational.of(1, 5), 5);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(36);
    GoalInterface rnGoal = RnMinDistGoalManager.sperical(stateGoal, radius);
    PlannerConstraint plannerConstraint = EmptyPlannerConstraint.INSTANCE;
    // ---
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    TrajectoryPlanner trajectoryPlanner = CheckedTrajectoryPlanner.wrap(new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, plannerConstraint, rnGoal));
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    List<GlcNode> nodeList = new ArrayList<>(trajectoryPlanner.getDomainMap().values());
    assertTrue(nodeList.get(0).isRoot());
    // nodeList.get(0).makeRoot(); // no error
    assertTrue(nodeList.get(0).isRoot());
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1);
    nodeList = new ArrayList<>(trajectoryPlanner.getDomainMap().values());
    GlcNode test = nodeList.getLast();
    assertFalse(test.isRoot());
    HeuristicAssert.check(trajectoryPlanner);
    glcExpand.findAny(10);
    int expandCount = glcExpand.getExpandCount();
    assertEquals(expandCount, 11);
    HeuristicAssert.check(trajectoryPlanner);
  }
}
