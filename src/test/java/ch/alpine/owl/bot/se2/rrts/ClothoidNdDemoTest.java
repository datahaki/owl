// code by jph
package ch.alpine.owl.bot.se2.rrts;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class ClothoidNdDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClothoidNdDemo());
  }
}
