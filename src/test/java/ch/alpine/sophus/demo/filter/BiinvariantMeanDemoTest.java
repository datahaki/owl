// code by jph
package ch.alpine.sophus.demo.filter;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class BiinvariantMeanDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BiinvariantMeanDemo());
  }
}
