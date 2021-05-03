// code by jph
package ch.alpine.sophus.app.decim;

import ch.alpine.sophus.app.io.GokartPoseDataV1;
import ch.alpine.sophus.app.io.GokartPoseDataV2;
import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class CurveDecimationDemoTest extends TestCase {
  public void testSimpleV1() {
    AbstractDemoHelper.offscreen(new CurveDecimationDemo(GokartPoseDataV1.INSTANCE));
  }

  public void testSimpleV2() {
    AbstractDemoHelper.offscreen(new CurveDecimationDemo(GokartPoseDataV2.INSTANCE));
  }
}
