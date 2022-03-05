// code by jph
package ch.alpine.owl.bot.se2.rl;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.gam.Factorial;
import junit.framework.TestCase;

public class CarDiscreteModelTest extends TestCase {
  public void testStateCount() {
    for (int n = 2; n < 7; ++n) {
      CarDiscreteModel cdm = new CarDiscreteModel(n, 2);
      Tensor states = cdm.states();
      assertEquals(states.length(), Scalars.intValueExact(Factorial.of(n).multiply(RationalScalar.HALF)) + 1);
    }
  }

  public void testZeroFail() {
    AssertFail.of(() -> new CarDiscreteModel(0, 2));
  }
}
