// code by jph
package ch.alpine.owl.bot.se2;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.glc.Se2CarFlows;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

public class Se2StateSpaceModelTest {
  @Test
  public void testQuantity() {
    Tensor x = Tensors.fromString("{-1[m], -2[m], 3}");
    Scalar h = Quantity.of(1, "s");
    Tensor flow = Se2CarFlows.singleton(Quantity.of(2, "m*s^-1"), Quantity.of(-1, "m^-1"));
    // Se2StateSpaceModel.INSTANCE.f(x, flow.getU());
    Tensor expl = Se2CarIntegrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Chop._04.requireClose(expl, impl);
  }
}
