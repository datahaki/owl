// code by jph
package ch.alpine.owl.bot.esp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;

class EspGoalAdapterTest {
  @Test
  void testGoal() {
    Tensor goal = EspGoalAdapter.GOAL;
    assertEquals(EspGoalAdapter.INSTANCE.minCostToGoal(goal), RealScalar.ZERO);
  }

  @Test
  void testStart() {
    assertTrue(Scalars.lessEquals( //
        RealScalar.of(0), //
        EspGoalAdapter.INSTANCE.minCostToGoal(EspDemo.START)));
  }
}
