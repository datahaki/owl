// code by jph
package ch.alpine.sophus.demo.ref.d1h;

import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class HermiteDatasetDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new HermiteDatasetDemo(GokartPoseDataV2.RACING_DAY));
  }
}
