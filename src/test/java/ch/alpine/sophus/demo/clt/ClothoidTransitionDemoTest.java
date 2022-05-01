// code by jph
package ch.alpine.sophus.demo.clt;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class ClothoidTransitionDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClothoidTransitionDemo());
  }
}
