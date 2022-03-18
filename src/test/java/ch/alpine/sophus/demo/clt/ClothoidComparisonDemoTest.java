// code by jph
package ch.alpine.sophus.demo.clt;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class ClothoidComparisonDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClothoidComparisonDemo());
  }
}
