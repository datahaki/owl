// code by jph
package ch.alpine.sophus.demo.bd1;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class R2AveragingDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R2AveragingDemo());
  }
}
