// code by jph
package ch.alpine.owl.gui.ren;

import org.junit.jupiter.api.Test;

class TrajectoryRenderTest {
  @Test
  public void testNull() {
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(null);
    trajectoryRender.render(null, null);
  }
}
