// code by jph
package ch.alpine.sophus.demo.misc;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class SnRotationDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SnRotationDemo());
  }
}
