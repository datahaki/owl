// code by jph
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OrderComparisonTest {
  @Test
  public void testSimple() {
    assertEquals(OrderComparison.values().length, 4);
  }
}
