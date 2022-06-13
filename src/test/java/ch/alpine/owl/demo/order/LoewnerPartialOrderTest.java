// code by jph
package ch.alpine.owl.demo.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.order.OrderComparison;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.DiagonalMatrix;

class LoewnerPartialOrderTest {
  @Test
  void testDyn() {
    Tensor x = DiagonalMatrix.of(1, 2);
    Tensor y = DiagonalMatrix.of(2, 1);
    OrderComparison orderComparison = LoewnerPartialOrder.INSTANCE.compare(x, y);
    assertEquals(orderComparison, OrderComparison.INCOMPARABLE);
  }

  @Test
  void testDyn2() {
    Tensor x = DiagonalMatrix.of(1, 1);
    Tensor y = DiagonalMatrix.of(2, 2);
    OrderComparison orderComparison = LoewnerPartialOrder.INSTANCE.compare(x, y);
    assertEquals(orderComparison, OrderComparison.STRICTLY_SUCCEEDS);
  }
}
