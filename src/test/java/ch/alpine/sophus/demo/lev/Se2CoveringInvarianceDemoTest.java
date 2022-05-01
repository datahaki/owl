// code by jph
package ch.alpine.sophus.demo.lev;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class Se2CoveringInvarianceDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Se2CoveringInvarianceDemo());
  }
}
