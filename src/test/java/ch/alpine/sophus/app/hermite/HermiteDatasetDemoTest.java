// code by jph
package ch.alpine.sophus.app.hermite;

import ch.alpine.sophus.app.io.GokartPoseDataV2;
import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class HermiteDatasetDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new HermiteDatasetDemo(GokartPoseDataV2.RACING_DAY));
  }
}
