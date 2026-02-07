// code by jph
package ch.alpine.owl.region;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InvertedRegionTest {
  @Test
  void testSimple() {
    assertTrue(new InvertedRegion<>(Regions.emptyRegion()).test(null));
    assertFalse(new InvertedRegion<>(Regions.completeRegion()).test(null));
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> new InvertedRegion<>(null));
  }
}
