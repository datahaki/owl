// code by jph
package ch.alpine.owl.demo.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.order.OrderComparison;

class EqualityOrderTest {
  @Test
  public void testInteger() {
    assertEquals(EqualityOrder.INSTANCE.compare(2, 3), OrderComparison.INCOMPARABLE);
    assertEquals(EqualityOrder.INSTANCE.compare(2, 2), OrderComparison.INDIFFERENT);
  }

  @Test
  public void testObject() {
    assertEquals(EqualityOrder.INSTANCE.compare("asd", 3), OrderComparison.INCOMPARABLE);
    assertEquals(EqualityOrder.INSTANCE.compare(2, "asdd"), OrderComparison.INCOMPARABLE);
  }

  @Test
  public void testNullFail() {
    assertThrows(Exception.class, () -> EqualityOrder.INSTANCE.compare(null, null));
    assertThrows(Exception.class, () -> EqualityOrder.INSTANCE.compare("abc", null));
    assertThrows(Exception.class, () -> EqualityOrder.INSTANCE.compare(null, "abc"));
  }
}
