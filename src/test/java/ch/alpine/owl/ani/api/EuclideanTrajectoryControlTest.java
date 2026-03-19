// code by jph
package ch.alpine.owl.ani.api;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.ani.api.EuclideanTrajectoryControl;

class EuclideanTrajectoryControlTest {
  @Test
  void testSimple() {
    EuclideanTrajectoryControl euclideanTrajectoryControl = new EuclideanTrajectoryControl();
    assertFalse(euclideanTrajectoryControl.customControl(null, null).isPresent());
  }
}
