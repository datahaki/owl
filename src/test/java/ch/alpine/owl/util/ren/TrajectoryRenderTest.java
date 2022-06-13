// code by jph
package ch.alpine.owl.util.ren;

import org.junit.jupiter.api.Test;

class TrajectoryRenderTest {
  @Test
  void testNull() {
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(null);
    trajectoryRender.render(null, null);
  }
}
