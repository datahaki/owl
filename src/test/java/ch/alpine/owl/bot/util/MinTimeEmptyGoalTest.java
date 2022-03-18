// code by jph
package ch.alpine.owl.bot.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

public class MinTimeEmptyGoalTest {
  @Test
  public void testSimple() {
    assertEquals(MinTimeEmptyGoal.INSTANCE.minCostToGoal(null), RealScalar.ZERO);
    assertFalse(MinTimeEmptyGoal.INSTANCE.test(null));
    assertFalse(MinTimeEmptyGoal.INSTANCE.firstMember(null).isPresent());
  }
}
