// code by jph
package ch.alpine.owl.bot.se2.rl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.gam.Factorial;

class CarDiscreteModelTest {
  @Test
  void testStateCount() {
    for (int n = 2; n < 7; ++n) {
      CarDiscreteModel cdm = new CarDiscreteModel(n, 2);
      Tensor states = cdm.states();
      assertEquals(states.length(), Scalars.intValueExact(Factorial.of(n).multiply(Rational.HALF)) + 1);
    }
  }

  @Test
  void testZeroFail() {
    assertThrows(Exception.class, () -> new CarDiscreteModel(0, 2));
  }
}
