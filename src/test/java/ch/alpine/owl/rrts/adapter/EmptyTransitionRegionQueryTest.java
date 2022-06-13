// code by jph
package ch.alpine.owl.rrts.adapter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EmptyTransitionRegionQueryTest {
  @Test
  void testSimple() {
    assertTrue(EmptyTransitionRegionQuery.INSTANCE.isDisjoint(null));
  }
}
