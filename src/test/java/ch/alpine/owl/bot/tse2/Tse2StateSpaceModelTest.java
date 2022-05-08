// code by jph
package ch.alpine.owl.bot.tse2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class Tse2StateSpaceModelTest {
  @Test
  public void testSimple() {
    Tensor tensor = Tse2StateSpaceModel.INSTANCE.f(Tensors.vector(1, 2, Math.PI, 5), Tensors.vector(3, 4));
    Chop._13.requireClose(tensor, Tensors.vector(-5, 0, 5 * 3, 4));
  }

  @Test
  public void testQuantity() {
    FlowsInterface flowsInterface = //
        Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.of(Quantity.of(-2, "m*s^-2"), Quantity.of(0, "m*s^-2"), Quantity.of(2, "m*s^-2")));
    Collection<Tensor> collection = flowsInterface.getFlows(3);
    for (Tensor flow : collection) {
      Tensor x = Tensors.fromString("{2[m], 3[m], 4, 3[m*s^-1]}").unmodifiable();
      Tensor u = flow.unmodifiable();
      Tensor f = Tse2StateSpaceModel.INSTANCE.f(x, u).unmodifiable();
      Scalar h = Quantity.of(1, "s");
      Tensor xp = x.add(f.multiply(h));
      Tensor xn = EulerIntegrator.INSTANCE.step(Tse2StateSpaceModel.INSTANCE, x, u, h);
      assertEquals(xp, xn);
    }
  }
}
