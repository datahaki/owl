// code by jph
package ch.alpine.sophus.demo.lev;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class Se2CoveringAnimationDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Se2CoveringAnimationDemo());
  }
}
