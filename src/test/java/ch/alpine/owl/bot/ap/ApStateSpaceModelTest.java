// code by astoll
package ch.alpine.owl.bot.ap;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class ApStateSpaceModelTest extends TestCase {
  public void testSimple() {
    Tensor f = ApStateSpaceModel.INSTANCE.f(Tensors.vector(80, 50, 30, 0.1), Tensors.vector(0, 0));
    assertEquals(4, f.length());
  }
}
