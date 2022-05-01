// code by jph
package ch.alpine.sophus.demo.bd2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.sca.Clips;

class ClipPointCoverTest {
  @Test
  public void testSimple() {
    assertEquals(ClipPointCover.of(Clips.interval(2, 4), RealScalar.of(1)), Clips.interval(1, 4));
    assertEquals(ClipPointCover.of(Clips.interval(2, 4), RealScalar.of(3)), Clips.interval(2, 4));
    assertEquals(ClipPointCover.of(Clips.interval(2, 4), RealScalar.of(5)), Clips.interval(2, 5));
  }
}
