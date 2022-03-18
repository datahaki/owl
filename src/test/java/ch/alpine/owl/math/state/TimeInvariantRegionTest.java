// code by jph
package ch.alpine.owl.math.state;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;

public class TimeInvariantRegionTest {
  @Test
  public void testFailNull() {
    AssertFail.of(() -> new TimeInvariantRegion(null));
  }
}
