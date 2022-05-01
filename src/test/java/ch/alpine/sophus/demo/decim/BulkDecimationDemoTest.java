// code by jph
package ch.alpine.sophus.demo.decim;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class BulkDecimationDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BulkDecimationDemo());
  }
}
