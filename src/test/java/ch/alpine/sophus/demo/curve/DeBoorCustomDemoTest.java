// code by jph
package ch.alpine.sophus.demo.curve;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class DeBoorCustomDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new DeBoorCustomDemo());
  }
}
