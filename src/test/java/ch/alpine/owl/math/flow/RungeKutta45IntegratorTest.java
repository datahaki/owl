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

public class RungeKutta45IntegratorTest {
  @Test
  public void testSe2() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Tensor u = Tensors.fromString("{1[m*s^-1], 0, 2[rad*s^-1]}").map(UnitSystem.SI());
    Tensor x = Tensors.fromString("{1[m], 2[m], 3[rad]}").map(UnitSystem.SI());
    Tensor r = EulerIntegrator.INSTANCE.step(stateSpaceModel, x, u, Quantity.of(2, "s"));
    Chop._10.requireClose(r, Tensors.fromString("{-0.9799849932008908[m], 2.2822400161197343[m], 7}"));
  }

  @Test
  public void testSe2Rk() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Tensor u = Tensors.fromString("{1[m*s^-1], 0, 2[rad*s^-1]}").map(UnitSystem.SI());
    Tensor x = Tensors.fromString("{1[m], 2[m], 3[rad]}").map(UnitSystem.SI());
    Tensor r = RungeKutta45Integrator.INSTANCE.step(stateSpaceModel, x, u, Quantity.of(2, "s"));
    Chop._10.requireClose(r, Tensors.fromString("{1.2568926185541083[m], 1.1315706479838576[m], 7}"));
  }
}
