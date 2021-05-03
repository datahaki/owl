// code by jph
package ch.alpine.owl.math.state;

import ch.alpine.owl.math.AssertFail;
import junit.framework.TestCase;

public class SimpleTrajectoryRegionQueryTest extends TestCase {
  public void testSimple() {
    AssertFail.of(() -> new SimpleTrajectoryRegionQuery(null));
  }
}
