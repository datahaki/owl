// code by jph
package ch.alpine.owl.math.flow;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Chop;

class RungeKutta4IntegratorTest {
  @Test
  void testSe2Rk() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Tensor u = Tensors.fromString("{1[m*s^-1], 0, 2[rad*s^-1]}").map(UnitSystem.SI());
    Tensor x = Tensors.fromString("{1[m], 2[m], 3[rad]}").map(UnitSystem.SI());
    Tensor r = RungeKutta4Integrator.INSTANCE.step(stateSpaceModel, x, u, Quantity.of(2, "s"));
    Chop._10.requireClose(r, Tensors.fromString("{1.2995194998652546[m], 0.9874698360420342[m], 7}"));
  }
}
