// code by jph
package ch.alpine.owl.math.region;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InvertedRegionTest {
  @Test
  public void testSimple() {
    assertTrue(new InvertedRegion<>(Regions.emptyRegion()).test(null));
    assertFalse(new InvertedRegion<>(Regions.completeRegion()).test(null));
  }

  @Test
  public void testNullFail() {
    assertThrows(Exception.class, () -> new InvertedRegion<>(null));
  }
}
