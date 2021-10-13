// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.owl.math.AssertFail;
import junit.framework.TestCase;

public class InvertedRegionTest extends TestCase {
  public void testSimple() {
    assertTrue(new InvertedRegion<>(Regions.emptyRegion()).test(null));
    assertFalse(new InvertedRegion<>(Regions.completeRegion()).test(null));
  }

  public void testNullFail() {
    AssertFail.of(() -> new InvertedRegion<>(null));
  }
}
