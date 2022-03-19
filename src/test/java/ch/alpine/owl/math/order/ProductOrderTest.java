// code by astoll
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ProductOrderTest {
  private static void _checkSym(OrderComparison a, OrderComparison b, OrderComparison ab) {
    assertEquals(ProductOrder.intersect(a, b), ab);
    assertEquals(ProductOrder.intersect(b, a), ab);
  }

  @Test
  public void testSimple() {
    _checkSym(OrderComparison.INDIFFERENT, OrderComparison.STRICTLY_PRECEDES, OrderComparison.STRICTLY_PRECEDES);
    _checkSym(OrderComparison.INDIFFERENT, OrderComparison.INCOMPARABLE, OrderComparison.INCOMPARABLE);
    _checkSym(OrderComparison.INCOMPARABLE, OrderComparison.INCOMPARABLE, OrderComparison.INCOMPARABLE);
    _checkSym(OrderComparison.STRICTLY_PRECEDES, OrderComparison.STRICTLY_PRECEDES, OrderComparison.STRICTLY_PRECEDES);
    _checkSym(OrderComparison.STRICTLY_PRECEDES, OrderComparison.STRICTLY_SUCCEEDS, OrderComparison.INCOMPARABLE);
    _checkSym(OrderComparison.STRICTLY_SUCCEEDS, OrderComparison.STRICTLY_SUCCEEDS, OrderComparison.STRICTLY_SUCCEEDS);
  }

  @Test
  public void testNullFail() {
    for (OrderComparison orderComparison : OrderComparison.values()) {
      assertThrows(Exception.class, () -> ProductOrder.intersect(orderComparison, null));
      assertThrows(Exception.class, () -> ProductOrder.intersect(null, orderComparison));
    }
  }
}
