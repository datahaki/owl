// code by jph
package ch.alpine.owl.rrts.adapter;

import junit.framework.TestCase;

public class EmptyTransitionRegionQueryTest extends TestCase {
  public void testSimple() {
    assertTrue(EmptyTransitionRegionQuery.INSTANCE.isDisjoint(null));
  }
}
