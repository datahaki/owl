// code by jph
package ch.alpine.owl.bot.tse2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;

public class Tse2ComboRegionTest {
  @Test
  public void testIsMember() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical(Tensors.vector(1, 2, 3, 1), Tensors.vector(1, 1, 0.1, 1));
    assertTrue(tse2ComboRegion.test(Tensors.vector(1, 2, 3, 1)));
    assertFalse(tse2ComboRegion.test(Tensors.vector(1, 2, 3, 2.1)));
    assertFalse(tse2ComboRegion.test(Tensors.vector(1, 2, 3, -0.1)));
    assertFalse(tse2ComboRegion.test(Tensors.vector(-1, 2, 3, 1)));
    assertFalse(tse2ComboRegion.test(Tensors.vector(1, 2, 3.2, 1)));
  }
}
