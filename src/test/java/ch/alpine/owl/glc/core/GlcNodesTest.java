// code by jph
package ch.alpine.owl.glc.core;

import java.util.Collections;
import java.util.List;

import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.bot.rn.RnNoHeuristicCircleGoalManager;
import ch.alpine.owl.bot.se2.Se2ShiftCostFunction;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GlcNodesTest extends TestCase {
  public void testCostIncrement1() {
    GlcNode root = GlcNodes.createRoot(new StateTime(Tensors.vector(2, 2), RealScalar.ZERO), x -> RealScalar.ZERO);
    GoalInterface rnGoal = RnMinDistGoalManager.sperical(Tensors.vector(5, 0), RealScalar.of(2));
    Scalar incr = rnGoal.costIncrement( //
        root, Collections.singletonList(new StateTime(Tensors.vector(10, 2), RealScalar.ZERO)), null);
    assertEquals(incr, RealScalar.of(8));
  }

  public void testCostIncrement2() {
    GlcNode root = GlcNodes.createRoot(new StateTime(Tensors.vector(2, 2), RealScalar.ZERO), x -> RealScalar.ZERO);
    RnNoHeuristicCircleGoalManager rnGoal = new RnNoHeuristicCircleGoalManager(Tensors.vector(5, 0), RealScalar.of(2));
    Scalar incr = rnGoal.costIncrement( //
        root, Collections.singletonList(new StateTime(Tensors.vector(10, 2), RealScalar.ZERO)), null);
    assertEquals(incr, RealScalar.of(8));
  }

  public void testCreateRoot() {
    GlcNode glcNode = GlcNodes.createRoot( //
        new StateTime(Tensors.vector(1, 2), RealScalar.of(10)), //
        x -> RealScalar.ZERO);
    StateTime last = new StateTime(Tensors.vector(1, 2), RealScalar.of(15));
    Scalar dt = StateTimeTrajectories.timeIncrement(glcNode, Collections.singletonList(last));
    assertEquals(dt, RealScalar.of(5));
  }

  public void testSimple2() {
    CostFunction costFunction = new Se2ShiftCostFunction(Quantity.of(100, "CHF"));
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ONE), costFunction);
    Scalar scalar = costFunction.costIncrement(glcNode, null, null);
    assertEquals(scalar, Quantity.of(0, "CHF"));
  }

  public void testRootFail() {
    AssertFail.of(() -> GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ONE), null));
  }

  public void testRoot() {
    GlcNode root = GlcNode.of(null, new StateTime(Tensors.empty(), RealScalar.ZERO), //
        RealScalar.ZERO, RealScalar.ZERO);
    List<StateTime> list = GlcNodes.getPathFromRootTo(root);
    assertEquals(list.size(), 1);
  }

  public void testSimple() {
    AssertFail.of(() -> GlcNodes.getPathFromRootTo(null));
  }
}
