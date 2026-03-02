// code by astoll
package ch.alpine.owl.bot.ap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owlets.math.flow.RungeKutta45Integrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

class ApStateSpaceModelTest {
  @Test
  void testSimple() {
    Tensor INITIAL = Tensors.fromString("{0[m], 80[m], 60[m*s^-1], -0.017}");
    Tensor u = Tensors.fromString("{2[kg*m*s^-2],0.01}");
    StateTime stateTime = new StateTime(INITIAL, Quantity.of(0, "s"));
    Tensor f = ApStateSpaceModel.INSTANCE.f(INITIAL, u);
    assertEquals(4, f.length());
    Scalar h = Quantity.of(1, "s");
    Tensor df = f.multiply(h);
    INITIAL.add(df);
    RungeKutta45Integrator.INSTANCE.step(ApStateSpaceModel.INSTANCE, INITIAL, u, h);
  }
}
