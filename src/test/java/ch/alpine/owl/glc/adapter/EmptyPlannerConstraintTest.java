// code by jph
package ch.alpine.owl.glc.adapter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EmptyPlannerConstraintTest {
  @Test
  public void testSimple() {
    assertTrue(EmptyPlannerConstraint.INSTANCE.isSatisfied(null, null, null));
  }
}
