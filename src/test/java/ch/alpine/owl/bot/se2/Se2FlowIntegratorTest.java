// code by jph
package ch.alpine.owl.bot.se2;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.sophus.lie.so2.So2;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Chop;

class Se2FlowIntegratorTest {
  @Test
  void testSe2Exact() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Tensor flow = Tensors.fromString("{1[m*s^-1], 0[m*s^-1], 2[rad*s^-1]}").map(UnitSystem.SI());
    Tensor x = Tensors.fromString("{1[m], 2[m], 3[rad]}").map(UnitSystem.SI());
    Tensor r = Se2FlowIntegrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, Quantity.of(2, "s"));
    Chop._10.requireClose(r, //
        Tensors.fromString("{1.2579332953294609[m], 1.128052624528125[m], " + So2.MOD.apply(RealScalar.of(7)) + "}"));
  }
}
