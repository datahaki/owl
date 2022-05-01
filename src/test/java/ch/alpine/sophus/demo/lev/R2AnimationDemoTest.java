// code by jph
package ch.alpine.sophus.demo.lev;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class R2AnimationDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R2AnimationDemo());
  }
}
