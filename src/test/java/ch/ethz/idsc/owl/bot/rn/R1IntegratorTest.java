// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
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
