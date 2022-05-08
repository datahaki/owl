// code by jph
package ch.alpine.owl.bot.tse2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Round;

class Tse2CarFlowsTest {
  @Test
  public void testSimple() {
    FlowsInterface flowsInterface = Tse2CarFlows.of(RealScalar.of(3), Tensors.vector(-2, 0, 1));
    Collection<Tensor> flows = flowsInterface.getFlows(10);
    assertEquals(Tse2Controls.maxAcc(flows), RealScalar.of(1));
    assertEquals(Tse2Controls.minAcc(flows), RealScalar.of(-2));
    assertEquals(Tse2Controls.maxTurning(flows), RealScalar.of(3));
  }

  @Test
  public void testQuantity() {
    FlowsInterface flowsInterface = //
        Tse2CarFlows.of(Quantity.of(3, "m^-1"), Tensors.fromString("{-2[m*s^-2], 0[m*s^-2], 2[m*s^-2]}"));
    Collection<Tensor> flows = flowsInterface.getFlows(1);
    assertEquals(flows.size(), 9);
    Tensor x = Tensors.fromString("{3[m], 4[m], -1, 3[m*s^-1]}");
    for (Tensor flow : flows) {
      Tensor dx = Tse2StateSpaceModel.INSTANCE.f(x, flow);
      Tensor xp = x.add(dx.multiply(Quantity.of(2, "s")));
      assertEquals(xp.extract(0, 2).map(Round._8), Tensors.fromString("{6.24181384[m], -1.04882591[m]}"));
    }
    {
      Scalar maxAcc = Tse2Controls.maxAcc(flows);
      assertEquals(maxAcc, Quantity.of(2, "m*s^-2"));
    }
    {
      Scalar minAcc = Tse2Controls.minAcc(flows);
      assertEquals(minAcc, Quantity.of(-2, "m*s^-2"));
    }
    {
      Scalar maxTurning = Tse2Controls.maxTurning(flows);
      assertEquals(maxTurning, Quantity.of(3, "m^-1"));
    }
  }

  @Test
  public void testFail() {
    assertThrows(Exception.class, () -> Tse2CarFlows.of(Quantity.of(1, "m^-1"), Quantity.of(2, "m*s^-2")));
  }
}
