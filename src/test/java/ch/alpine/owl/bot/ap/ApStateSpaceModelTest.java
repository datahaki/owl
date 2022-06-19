// code by astoll
package ch.alpine.owl.bot.ap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class ApStateSpaceModelTest {
  @Test
  void testSimple() {
    Tensor f = ApStateSpaceModel.INSTANCE.f(Tensors.vector(80, 50, 30, 0.1), Tensors.vector(0, 0));
    assertEquals(4, f.length());
  }
}
