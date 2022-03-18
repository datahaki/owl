// code by jph
package ch.alpine.sophus.demo.misc;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class Se2UnprojectDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Se2UnprojectDemo());
  }
}
