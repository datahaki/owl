// code by jph
package ch.alpine.owl.bot.rn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.HeuristicQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

class RnNoHeuristicCircleGoalManagerTest {
  @Test
  public void testMinCostToGoal1() {
    GoalInterface rnGoal = RnMinDistGoalManager.sperical(Tensors.vector(5, 0), RealScalar.of(2));
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(2, 0)), RealScalar.ONE);
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(3, 0)), RealScalar.ZERO);
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(4, 0)), RealScalar.ZERO);
  }

  @Test
  public void testMinCostToGoal2() {
    RnNoHeuristicCircleGoalManager rnGoal = new RnNoHeuristicCircleGoalManager(Tensors.vector(5, 0), RealScalar.of(2));
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(2, 0)), RealScalar.ZERO);
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(3, 0)), RealScalar.ZERO);
    assertEquals(rnGoal.minCostToGoal(Tensors.vector(4, 0)), RealScalar.ZERO);
  }

  @Test
  public void testNoHeuristic1() {
    RnNoHeuristicCircleGoalManager rnGoal = new RnNoHeuristicCircleGoalManager(Tensors.vector(5, 0), RealScalar.of(2));
    assertFalse(HeuristicQ.of(rnGoal));
  }
}
