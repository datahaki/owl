// code by jph
package ch.alpine.owl.bot.rice;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;

class Rice2StateSpaceModelTest {
  @Test
  public void testFails() {
    Rice2StateSpaceModel.of(RealScalar.ZERO);
    Rice2StateSpaceModel.of(RealScalar.of(-1));
  }
}
