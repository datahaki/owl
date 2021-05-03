// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.owl.math.AssertFail;
import junit.framework.TestCase;

public class InvertedRegionTest extends TestCase {
  public void testSimple() {
    assertTrue(new InvertedRegion<>(Regions.emptyRegion()).isMember(null));
    assertFalse(new InvertedRegion<>(Regions.completeRegion()).isMember(null));
  }

  public void testNullFail() {
    AssertFail.of(() -> new InvertedRegion<>(null));
  }
}
