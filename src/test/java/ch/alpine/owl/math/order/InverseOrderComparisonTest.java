// code by jph
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InverseOrderComparisonTest {
  @Test
  void testSimple() {
    assertEquals(InverseOrderComparison.of(OrderComparison.STRICTLY_PRECEDES), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(InverseOrderComparison.of(OrderComparison.INDIFFERENT), OrderComparison.INDIFFERENT);
    assertEquals(InverseOrderComparison.of(OrderComparison.STRICTLY_SUCCEEDS), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(InverseOrderComparison.of(OrderComparison.INCOMPARABLE), OrderComparison.INCOMPARABLE);
  }
}
