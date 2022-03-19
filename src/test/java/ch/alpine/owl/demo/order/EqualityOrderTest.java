// code by jph
package ch.alpine.owl.demo.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.order.OrderComparison;

public class EqualityOrderTest {
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
    AssertFail.of(() -> EqualityOrder.INSTANCE.compare(null, null));
    AssertFail.of(() -> EqualityOrder.INSTANCE.compare("abc", null));
    AssertFail.of(() -> EqualityOrder.INSTANCE.compare(null, "abc"));
  }
}
