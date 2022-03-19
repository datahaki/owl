// code by jph
package ch.alpine.owl.bot.tse2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

public class Tse2IntegratorTest {
  @Test
  public void testQuantity() throws ClassNotFoundException, IOException {
    FlowsInterface flowsInterface = //
        Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.of(Quantity.of(-2, "m*s^-2"), Quantity.of(0, "m*s^-2"), Quantity.of(2, "m*s^-2")));
    Collection<Tensor> collection = flowsInterface.getFlows(3);
    Tse2Integrator tse2Integrator = //
        Serialization.copy(new Tse2Integrator(Clips.interval(Quantity.of(-20, "m*s^-1"), Quantity.of(20, "m*s^-1"))));
    for (Tensor flow : collection) {
      Tensor x = Tensors.fromString("{2[m], 3[m], 4, 3[m*s^-1]}").unmodifiable();
      Tensor u = flow.unmodifiable();
      Tensor f = Tse2StateSpaceModel.INSTANCE.f(x, u).unmodifiable();
      Scalar h = Quantity.of(0.1, "s");
      Tensor xp = x.add(f.multiply(h));
      Tensor xn = EulerIntegrator.INSTANCE.step(Tse2StateSpaceModel.INSTANCE, x, flow, h);
      assertEquals(xp, xn);
      Tensor xr = RungeKutta45Integrator.INSTANCE.step(Tse2StateSpaceModel.INSTANCE, x, flow, h);
      Tensor xt = tse2Integrator.step(Tse2StateSpaceModel.INSTANCE, x, flow, h);
      Tensor xd = xr.subtract(xt);
      Chop._04.requireAllZero(xd);
    }
  }
}
