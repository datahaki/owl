// code by jph
package ch.alpine.owl.math.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class SingleIntegratorStateSpaceModelTest {
  @Test
  public void testSimple() {
    Tensor u = Tensors.vector(1, 2, 3);
    Tensor r = SingleIntegratorStateSpaceModel.INSTANCE.f(null, u);
    assertEquals(u, r);
  }
}
