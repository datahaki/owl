// code by jph
package ch.alpine.owl.sim;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class CustomClothoidDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new CustomClothoidDemo());
  }
}
