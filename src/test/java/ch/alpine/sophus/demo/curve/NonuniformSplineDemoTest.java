// code by jph
package ch.alpine.sophus.demo.curve;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class NonuniformSplineDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new NonuniformSplineDemo());
  }
}
