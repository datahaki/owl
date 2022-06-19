// code by astoll
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class TotalOrderMinMaxTest {
  @Test
  void testMin() {
    Tensor test = Tensors.vector(1, 2, 3, 4, 0.2).unmodifiable();
    assertEquals(RealScalar.of(0.2), TotalOrderMinMax.min(test));
    assertEquals(RealScalar.of(4), TotalOrderMinMax.max(test));
  }
}
