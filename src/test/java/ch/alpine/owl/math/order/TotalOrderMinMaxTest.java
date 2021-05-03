// code by astoll
package ch.alpine.owl.math.order;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class TotalOrderMinMaxTest extends TestCase {
  public void testMin() {
    Tensor test = Tensors.vector(1, 2, 3, 4, 0.2).unmodifiable();
    assertEquals(RealScalar.of(0.2), TotalOrderMinMax.min(test));
    assertEquals(RealScalar.of(4), TotalOrderMinMax.max(test));
  }
}
