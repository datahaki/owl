// code by jph
package ch.alpine.owl.glc.adapter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owlets.glc.adapter.EmptyPlannerConstraint;

class EmptyPlannerConstraintTest {
  @Test
  void testSimple() {
    assertTrue(EmptyPlannerConstraint.INSTANCE.isSatisfied(null, null, null));
  }
}
