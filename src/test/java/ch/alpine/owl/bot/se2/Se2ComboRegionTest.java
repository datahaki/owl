// code by jph
package ch.alpine.owl.bot.se2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Chop;

class Se2ComboRegionTest {
  @Test
  void testSimple() {
    Se2ComboRegion.ball(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 3));
    assertThrows(Exception.class, () -> Se2ComboRegion.ball(Tensors.vector(1, 2, 3), Tensors.vector(1, 2, 3)));
  }

  @Test
  void testIsMember() {
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    assertTrue(se2ComboRegion.test(Tensors.vector(1, 2, 3)));
    assertFalse(se2ComboRegion.test(Tensors.vector(-1, 2, 3)));
    assertFalse(se2ComboRegion.test(Tensors.vector(1, 2, 3.2)));
  }

  @Test
  void testCone() {
    Se2ComboRegion se2ComboRegion = //
        Se2ComboRegion.cone(Tensors.vector(0, 0, 6 * Math.PI), RealScalar.of(Math.PI / 4), RealScalar.of(1));
    assertEquals(se2ComboRegion.d_angle(Tensors.vector(0, 0, 1.00)), DoubleScalar.of(0.00));
    assertEquals(se2ComboRegion.d_angle(Tensors.vector(0, 0, 1.25)), DoubleScalar.of(0.25));
    assertEquals(se2ComboRegion.d_angle(Tensors.vector(0, 0, -1.5)), DoubleScalar.of(0.5));
    assertEquals(se2ComboRegion.d_xy(Tensors.vector(-1, 0, 0.00)), RealScalar.ONE);
    Chop._10.requireClose(se2ComboRegion.d_xy(Tensors.vector(0, -1, 0.00)), RealScalar.of(Math.sqrt(0.5)));
  }
}
