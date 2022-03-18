// code by jph
package ch.alpine.sophus.demo.lev;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class GrassmannDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GrassmannDemo());
  }
}
