// code by jph
package ch.alpine.owl.math.state;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class EmptyTrajectoryRegionQueryTest {
  @Test
  void testSimple() {
    assertFalse(EmptyTrajectoryRegionQuery.INSTANCE.firstMember(null).isPresent());
    assertFalse(EmptyTrajectoryRegionQuery.INSTANCE.test(null));
  }
}
