// code by astoll
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.demo.order.ScalarTotalOrder;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.Abs;

class OrderTest {
  @Test
  void testTotalOrderIntegers() {
    OrderComparator<Integer> totalScalarComparator = new Order<>((x, y) -> x <= y);
    assertEquals(OrderComparison.STRICTLY_PRECEDES, totalScalarComparator.compare(4, 5));
    assertEquals(OrderComparison.INDIFFERENT, totalScalarComparator.compare(5, 5));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, totalScalarComparator.compare(6, 5));
  }

  @Test
  void testTotalOrderScalars() {
    OrderComparator<Scalar> totalScalarComparator = ScalarTotalOrder.INSTANCE;
    assertEquals(OrderComparison.STRICTLY_PRECEDES, totalScalarComparator.compare(RealScalar.of(5), RealScalar.of(7)));
    assertEquals(OrderComparison.INDIFFERENT, totalScalarComparator.compare(RealScalar.of(5), RealScalar.of(5)));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, totalScalarComparator.compare(RealScalar.of(5), RealScalar.of(-5)));
  }

  @Test
  void testPartialOrderScalars() {
    OrderComparator<Scalar> partialScalarComparator = new Order<>((x, y) -> Scalars.divides(Abs.FUNCTION.apply(x), Abs.FUNCTION.apply(y)));
    assertEquals(OrderComparison.STRICTLY_PRECEDES, partialScalarComparator.compare(RealScalar.of(2), RealScalar.of(4)));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, partialScalarComparator.compare(RealScalar.of(4), RealScalar.of(2)));
    assertEquals(OrderComparison.INDIFFERENT, partialScalarComparator.compare(RealScalar.of(5), RealScalar.of(5)));
    assertEquals(OrderComparison.INDIFFERENT, partialScalarComparator.compare(RealScalar.of(5), RealScalar.of(-5)));
    assertEquals(OrderComparison.INCOMPARABLE, partialScalarComparator.compare(RealScalar.of(5), RealScalar.of(7)));
    assertEquals(OrderComparison.INCOMPARABLE, partialScalarComparator.compare(RealScalar.of(7), RealScalar.of(5)));
  }

  @Test
  void testPreorderScalars() {
    OrderComparator<Scalar> preorderScalarComparator = new Order<>(Scalars::divides);
    assertEquals(OrderComparison.STRICTLY_PRECEDES, preorderScalarComparator.compare(RealScalar.of(2), RealScalar.of(4)));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, preorderScalarComparator.compare(RealScalar.of(4), RealScalar.of(2)));
    assertEquals(OrderComparison.INDIFFERENT, preorderScalarComparator.compare(RealScalar.of(5), RealScalar.of(5)));
    assertEquals(OrderComparison.INDIFFERENT, preorderScalarComparator.compare(RealScalar.of(5), RealScalar.of(-5)));
    assertEquals(OrderComparison.INCOMPARABLE, preorderScalarComparator.compare(RealScalar.of(5), RealScalar.of(7)));
    assertEquals(OrderComparison.INCOMPARABLE, preorderScalarComparator.compare(RealScalar.of(7), RealScalar.of(5)));
  }
}
