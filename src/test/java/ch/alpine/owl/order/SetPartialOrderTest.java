// code by astoll
package ch.alpine.owl.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.order.OrderComparison;
import ch.alpine.owl.math.order.SetPartialOrder;

class SetPartialOrderTest {
  static Collection<Integer> create(Integer... integers) {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(integers)));
  }

  Collection<Integer> A = create(1, 2, 3);
  Collection<Integer> B = create(1, 2, 3);
  Collection<Integer> C = create(1, 2);
  Collection<Integer> D = create(1, 2, 3, 8);
  Collection<Integer> E = create(5, 6, 7);

  @Test
  void testEquals() {
    OrderComparison comparison = SetPartialOrder.INSTANCE.compare(A, B);
    assertEquals(comparison, OrderComparison.INDIFFERENT);
  }

  @Test
  void testGreater() {
    OrderComparison comparison = SetPartialOrder.INSTANCE.compare(A, C);
    assertEquals(comparison, OrderComparison.STRICTLY_SUCCEEDS);
  }

  @Test
  void testLess() {
    OrderComparison comparison = SetPartialOrder.INSTANCE.compare(A, D);
    assertEquals(comparison, OrderComparison.STRICTLY_PRECEDES);
  }

  @Test
  void testNotComparable() {
    OrderComparison comparison = SetPartialOrder.INSTANCE.compare(A, E);
    assertEquals(comparison, OrderComparison.INCOMPARABLE);
  }
}
