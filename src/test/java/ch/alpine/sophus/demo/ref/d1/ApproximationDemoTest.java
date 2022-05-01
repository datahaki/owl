// code by jph
package ch.alpine.sophus.demo.ref.d1;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class ApproximationDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ApproximationDemo(GokartPoseDataV2.RACING_DAY));
  }
}
