// code by jph
package ch.alpine.owl.bot.esp;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import junit.framework.TestCase;

public class EspGoalAdapterTest extends TestCase {
  public void testGoal() {
    Tensor goal = EspGoalAdapter.GOAL;
    assertEquals(EspGoalAdapter.INSTANCE.minCostToGoal(goal), RealScalar.ZERO);
  }

  public void testStart() {
    assertTrue(Scalars.lessEquals( //
        RealScalar.of(0), //
        EspGoalAdapter.INSTANCE.minCostToGoal(EspDemo.START)));
  }
}
