// code by jph
package ch.alpine.owl.ani.adapter;

import junit.framework.TestCase;

public class EuclideanTrajectoryControlTest extends TestCase {
  public void testSimple() {
    EuclideanTrajectoryControl euclideanTrajectoryControl = new EuclideanTrajectoryControl();
    assertFalse(euclideanTrajectoryControl.customControl(null, null).isPresent());
  }
}
