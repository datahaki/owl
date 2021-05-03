// code by jph
package ch.alpine.owl.gui.ren;

import junit.framework.TestCase;

public class TrajectoryRenderTest extends TestCase {
  public void testNull() {
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(null);
    trajectoryRender.render(null, null);
  }
}
