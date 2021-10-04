// code by jph
package ch.alpine.sophus.demo.ref.d1;

import ch.alpine.sophus.app.io.GokartPoseDataV2;
import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class ApproximationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ApproximationDemo(GokartPoseDataV2.RACING_DAY));
  }
}
