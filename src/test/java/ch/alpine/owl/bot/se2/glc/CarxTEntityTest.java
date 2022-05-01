// code by jph
package ch.alpine.owl.bot.se2.glc;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

class CarxTEntityTest {
  @Test
  public void testSimple() {
    // CarxTEntity carxt =
    new CarxTEntity(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO));
    // Tensor eta = carxt.eta();
    // assertTrue(ExactScalarQ.of(eta.Get(3)));
  }
}
