// code by jph
package ch.alpine.owl.bot.rn;

import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class R1IntegratorTest extends TestCase {
  public void testSimple() {
    Tensor xn = R1Integrator.direct(Tensors.fromString("{3[m], 1[m*s^-1]}"), Quantity.of(2, "m*s^-2"), Quantity.of(10, "s"));
    assertEquals(xn, Tensors.fromString("{113[m], 21[m*s^-1]}"));
  }

  public void testIntegrator() {
    Integrator integrator = R1Integrator.INSTANCE;
    Tensor tensor = integrator.step( //
        SingleIntegratorStateSpaceModel.INSTANCE, Tensors.vector(10, 2), Tensors.vector(1), RationalScalar.HALF);
    assertEquals(tensor, Tensors.fromString("{89/8, 5/2}"));
    ExactTensorQ.require(tensor);
  }
}
