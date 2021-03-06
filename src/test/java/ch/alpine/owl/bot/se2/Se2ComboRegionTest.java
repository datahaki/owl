// code by jph
package ch.alpine.owl.bot.se2;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2ComboRegionTest extends TestCase {
  public void testSimple() {
    Se2ComboRegion.ball(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 3));
    AssertFail.of(() -> Se2ComboRegion.ball(Tensors.vector(1, 2, 3), Tensors.vector(1, 2, 3)));
  }

  public void testIsMember() {
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    assertTrue(se2ComboRegion.isMember(Tensors.vector(1, 2, 3)));
    assertFalse(se2ComboRegion.isMember(Tensors.vector(-1, 2, 3)));
    assertFalse(se2ComboRegion.isMember(Tensors.vector(1, 2, 3.2)));
  }

  public void testCone() {
    Se2ComboRegion se2ComboRegion = //
        Se2ComboRegion.cone(Tensors.vector(0, 0, 6 * Math.PI), RealScalar.of(Math.PI / 4), RealScalar.of(1));
    assertEquals(se2ComboRegion.d_angle(Tensors.vector(0, 0, 1.00)), DoubleScalar.of(0.00));
    assertEquals(se2ComboRegion.d_angle(Tensors.vector(0, 0, 1.25)), DoubleScalar.of(0.25));
    assertEquals(se2ComboRegion.d_angle(Tensors.vector(0, 0, -1.5)), DoubleScalar.of(0.5));
    assertEquals(se2ComboRegion.d_xy(Tensors.vector(-1, 0, 0.00)), RealScalar.ONE);
    Chop._10.requireClose(se2ComboRegion.d_xy(Tensors.vector(0, -1, 0.00)), RealScalar.of(Math.sqrt(0.5)));
  }
}
