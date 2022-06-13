// code by jph
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.demo.order.ScalarTotalOrder;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

class ProductOrderTrackerTest {
  @Test
  void testSimple() {
    ProductOrderTracker<Scalar> productOrderTracker = new ProductOrderTracker<>(ScalarTotalOrder.INSTANCE);
    assertEquals(productOrderTracker.digest(RealScalar.of(0), RealScalar.of(0)), OrderComparison.INDIFFERENT);
    assertEquals(productOrderTracker.digest(RealScalar.of(0), RealScalar.of(1)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(productOrderTracker.digest(RealScalar.of(0), RealScalar.of(0)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(productOrderTracker.digest(RealScalar.of(1), RealScalar.of(1)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(productOrderTracker.digest(RealScalar.of(1), RealScalar.of(0)), OrderComparison.INCOMPARABLE);
  }
}
