// code by jph
package ch.alpine.owl.bot.ip;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

class IpStateSpaceModelTest {
  @ParameterizedTest
  @EnumSource
  void test(TimeIntegrators timeIntegrators) {
    StateSpaceModel stateSpaceModel = IpStateSpaceModel.EXAMPLE;
    Tensor x = Tensors.fromString("{1[m], 0.3[m*s^-1], 0.03, 0.001[s^-1]}");
    Tensor u = Tensors.fromString("{2[kg*m*s^-2]}");
    Tensor f = stateSpaceModel.f(x, u);
    Tensor step = timeIntegrators.step(stateSpaceModel, x, u, Quantity.of(1, "s"));
    f.add(f);
    step.add(step);
  }
}
