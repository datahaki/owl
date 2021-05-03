// code by jph
package ch.alpine.owl.math.state;

import ch.alpine.owl.math.AssertFail;
import junit.framework.TestCase;

public class TimeInvariantRegionTest extends TestCase {
  public void testFailNull() {
    AssertFail.of(() -> new TimeInvariantRegion(null));
  }
}
