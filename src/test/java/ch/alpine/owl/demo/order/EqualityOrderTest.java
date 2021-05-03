// code by jph
package ch.alpine.owl.demo.order;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.order.OrderComparison;
import junit.framework.TestCase;

public class EqualityOrderTest extends TestCase {
  public void testInteger() {
    assertEquals(EqualityOrder.INSTANCE.compare(2, 3), OrderComparison.INCOMPARABLE);
    assertEquals(EqualityOrder.INSTANCE.compare(2, 2), OrderComparison.INDIFFERENT);
  }

  public void testObject() {
    assertEquals(EqualityOrder.INSTANCE.compare("asd", 3), OrderComparison.INCOMPARABLE);
    assertEquals(EqualityOrder.INSTANCE.compare(2, "asdd"), OrderComparison.INCOMPARABLE);
  }

  public void testNullFail() {
    AssertFail.of(() -> EqualityOrder.INSTANCE.compare(null, null));
    AssertFail.of(() -> EqualityOrder.INSTANCE.compare("abc", null));
    AssertFail.of(() -> EqualityOrder.INSTANCE.compare(null, "abc"));
  }
}
