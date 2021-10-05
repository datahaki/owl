// code by jph
package ch.alpine.sophus.demo.io;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GokartPoseDataV2Test extends TestCase {
  public void testSampleRate() {
    assertEquals(GokartPoseDataV2.INSTANCE.getSampleRate(), Quantity.of(50, "s^-1"));
  }

  public void testRacingLength() {
    assertTrue(18 <= GokartPoseDataV2.RACING_DAY.list().size());
  }

  public void testListUnmodifiable() {
    AssertFail.of(() -> GokartPoseDataV2.INSTANCE.list().clear());
    AssertFail.of(() -> GokartPoseDataV2.RACING_DAY.list().clear());
  }
}
