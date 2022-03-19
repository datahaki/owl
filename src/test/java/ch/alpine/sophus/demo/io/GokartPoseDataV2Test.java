// code by jph
package ch.alpine.sophus.demo.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.qty.Quantity;

public class GokartPoseDataV2Test {
  @Test
  public void testSampleRate() {
    assertEquals(GokartPoseDataV2.INSTANCE.getSampleRate(), Quantity.of(50, "s^-1"));
  }

  @Test
  public void testRacingLength() {
    assertTrue(18 <= GokartPoseDataV2.RACING_DAY.list().size());
  }

  @Test
  public void testListUnmodifiable() {
    AssertFail.of(() -> GokartPoseDataV2.INSTANCE.list().clear());
    AssertFail.of(() -> GokartPoseDataV2.RACING_DAY.list().clear());
  }
}
