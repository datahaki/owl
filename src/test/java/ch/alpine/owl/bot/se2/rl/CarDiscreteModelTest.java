// code by jph
package ch.alpine.owl.bot.se2.rl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.gam.Factorial;

public class CarDiscreteModelTest {
  @Test
  public void testStateCount() {
    for (int n = 2; n < 7; ++n) {
      CarDiscreteModel cdm = new CarDiscreteModel(n, 2);
      Tensor states = cdm.states();
      assertEquals(states.length(), Scalars.intValueExact(Factorial.of(n).multiply(RationalScalar.HALF)) + 1);
    }
  }

  @Test
  public void testZeroFail() {
    AssertFail.of(() -> new CarDiscreteModel(0, 2));
  }
}
