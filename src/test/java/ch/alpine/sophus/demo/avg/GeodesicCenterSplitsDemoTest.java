// code by jph
package ch.alpine.sophus.demo.avg;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class GeodesicCenterSplitsDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GeodesicCenterSplitsDemo());
  }
}
