// code by jph
package ch.alpine.owl.bot.rice;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class Rice1StateSpaceModelTest {
  @Test
  void testFormerFails() {
    Rice1StateSpaceModel.of(RealScalar.ZERO);
    Rice1StateSpaceModel.of(RealScalar.of(-1));
  }
}
