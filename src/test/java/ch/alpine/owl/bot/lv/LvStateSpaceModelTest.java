// code by jph
package ch.alpine.owl.bot.lv;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

class LvStateSpaceModelTest {
  @ParameterizedTest
  @EnumSource
  void testStateSpaceModel(TimeIntegrators timeIntegrators) {
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.EXAMPLE;
    Tensor x = Tensors.vector(10, 3);
    Tensor u = Tensors.of(Quantity.of(0.1, "s^-1"));
    stateSpaceModel.f(x, u);
    timeIntegrators.step(stateSpaceModel, x, u, Quantity.of(1, "s"));
  }
}
