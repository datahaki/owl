// code by jph
package ch.alpine.sophus.demo.decim;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.demo.io.GokartPoseDataV1;
import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class CurveDecimationDemoTest {
  @Test
  public void testSimpleV1() {
    AbstractDemoHelper.offscreen(new CurveDecimationDemo(GokartPoseDataV1.INSTANCE));
  }

  @Test
  public void testSimpleV2() {
    AbstractDemoHelper.offscreen(new CurveDecimationDemo(GokartPoseDataV2.INSTANCE));
  }
}
