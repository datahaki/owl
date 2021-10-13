// code by jph
package ch.alpine.sophus.demo.decim;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import ch.alpine.sophus.demo.io.GokartPoseDataV1;
import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import junit.framework.TestCase;

public class CurveDecimationDemoTest extends TestCase {
  public void testSimpleV1() {
    AbstractDemoHelper.offscreen(new CurveDecimationDemo(GokartPoseDataV1.INSTANCE));
  }

  public void testSimpleV2() {
    AbstractDemoHelper.offscreen(new CurveDecimationDemo(GokartPoseDataV2.INSTANCE));
  }
}
