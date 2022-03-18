// code by jph
package ch.alpine.owl.ani.adapter;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class EuclideanTrajectoryControlTest {
  @Test
  public void testSimple() {
    EuclideanTrajectoryControl euclideanTrajectoryControl = new EuclideanTrajectoryControl();
    assertFalse(euclideanTrajectoryControl.customControl(null, null).isPresent());
  }
}
