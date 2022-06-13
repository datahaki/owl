// code by jph
package ch.alpine.owl.math.state;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SimpleTrajectoryRegionQueryTest {
  @Test
  void testSimple() {
    assertThrows(Exception.class, () -> new SimpleTrajectoryRegionQuery(null));
  }
}
