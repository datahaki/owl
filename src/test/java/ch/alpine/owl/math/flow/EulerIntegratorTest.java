// code by jph
package ch.alpine.owl.math.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.model.DoubleIntegratorStateSpaceModel;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.sophus.lie.rn.RnGroup;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityTensor;
import ch.alpine.tensor.qty.Unit;

class EulerIntegratorTest {
  private final Integrator integrator = EulerLieIntegrator.of(RnGroup.INSTANCE);

  @Test
  public void testSimple() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor u = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-1"));
    Tensor x = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m"));
    Scalar h = Quantity.of(2, "s");
    Tensor r = EulerIntegrator.INSTANCE.step(stateSpaceModel, x, u, h);
    assertEquals(r, Tensors.fromString("{3[m], 6[m]}"));
    assertEquals(r, integrator.step(stateSpaceModel, x, u, h));
  }

  @Test
  public void testDouble() {
    StateSpaceModel stateSpaceModel = DoubleIntegratorStateSpaceModel.INSTANCE;
    Tensor u = QuantityTensor.of(Tensors.vector(1, 2), Unit.of("m*s^-2"));
    Tensor x = Tensors.fromString("{2[m], 3[m], 4[m*s^-1], 5[m*s^-1]}"); // pos and vel
    Tensor r = EulerIntegrator.INSTANCE.step(stateSpaceModel, x, u, Quantity.of(2, "s"));
    assertEquals(r, Tensors.fromString("{10[m], 13[m], 6[m*s^-1], 9[m*s^-1]}"));
  }
}
