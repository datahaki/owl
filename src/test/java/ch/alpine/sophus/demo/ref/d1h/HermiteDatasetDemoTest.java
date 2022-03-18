// code by jph
package ch.alpine.sophus.demo.ref.d1h;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class HermiteDatasetDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new HermiteDatasetDemo(GokartPoseDataV2.RACING_DAY));
  }
}
