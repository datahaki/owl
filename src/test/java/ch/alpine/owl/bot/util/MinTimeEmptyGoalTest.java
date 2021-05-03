// code by jph
package ch.alpine.owl.bot.util;

import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class MinTimeEmptyGoalTest extends TestCase {
  public void testSimple() {
    assertEquals(MinTimeEmptyGoal.INSTANCE.minCostToGoal(null), RealScalar.ZERO);
    assertFalse(MinTimeEmptyGoal.INSTANCE.isMember(null));
    assertFalse(MinTimeEmptyGoal.INSTANCE.firstMember(null).isPresent());
  }
}
