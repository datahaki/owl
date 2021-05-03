// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class CarxTEntityTest extends TestCase {
  public void testSimple() {
    // CarxTEntity carxt =
    new CarxTEntity(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO));
    // Tensor eta = carxt.eta();
    // assertTrue(ExactScalarQ.of(eta.Get(3)));
  }
}
