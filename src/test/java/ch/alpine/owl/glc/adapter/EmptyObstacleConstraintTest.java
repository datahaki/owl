// code by jph
package ch.alpine.owl.glc.adapter;

import junit.framework.TestCase;

public class EmptyObstacleConstraintTest extends TestCase {
  public void testSimple() {
    assertTrue(EmptyPlannerConstraint.INSTANCE.isSatisfied(null, null, null));
  }
}
