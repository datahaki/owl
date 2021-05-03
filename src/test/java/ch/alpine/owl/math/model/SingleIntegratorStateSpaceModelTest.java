// code by jph
package ch.alpine.owl.math.model;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class SingleIntegratorStateSpaceModelTest extends TestCase {
  public void testSimple() {
    Tensor u = Tensors.vector(1, 2, 3);
    Tensor r = SingleIntegratorStateSpaceModel.INSTANCE.f(null, u);
    assertEquals(u, r);
  }
}
