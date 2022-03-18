// code by jph
package ch.alpine.owl.math.state;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;

public class SimpleTrajectoryRegionQueryTest {
  @Test
  public void testSimple() {
    AssertFail.of(() -> new SimpleTrajectoryRegionQuery(null));
  }
}
