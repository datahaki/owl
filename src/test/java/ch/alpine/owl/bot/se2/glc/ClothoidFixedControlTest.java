// code by jph
package ch.alpine.owl.bot.se2.glc;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class ClothoidFixedControlTest {
  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> new ClothoidFixedControl(null, RealScalar.of(2)));
    assertThrows(Exception.class, () -> new ClothoidFixedControl(RealScalar.of(2), null));
  }
}
