// code by jph
package ch.alpine.sophus.demo.lev;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class S2AnimationDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new S2AnimationDemo());
  }
}
