// code by jph
package ch.alpine.sophus.demo.ref.d1;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import junit.framework.TestCase;

public class ApproximationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ApproximationDemo(GokartPoseDataV2.RACING_DAY));
  }
}
