// code by jph
package ch.alpine.sophus.demo.bd2;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClipCoverTest extends TestCase {
  public void testSimple() {
    assertEquals(ClipCover.of(Clips.interval(2, 4), RealScalar.of(1)), Clips.interval(1, 4));
    assertEquals(ClipCover.of(Clips.interval(2, 4), RealScalar.of(3)), Clips.interval(2, 4));
    assertEquals(ClipCover.of(Clips.interval(2, 4), RealScalar.of(5)), Clips.interval(2, 5));
  }
}
