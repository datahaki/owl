// code by jph
package ch.alpine.owl.demo.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.order.OrderComparison;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

class TypeStrictPartialOrderTest {
  @Test
  void testSimple() {
    assertFalse(Scalar.class.isAssignableFrom(Tensor.class));
    assertTrue(Scalar.class.isAssignableFrom(Scalar.class));
    assertTrue(Tensor.class.isAssignableFrom(Scalar.class));
  }

  @Test
  void testCompare() {
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(Tensor.class, Scalar.class), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(Object.class, Scalar.class), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(Tensor.class, Object.class), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(List.class, Object.class), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(List.class, List.class), OrderComparison.INDIFFERENT);
    assertEquals(TypeStrictPartialOrder.INSTANCE.compare(List.class, Tensor.class), OrderComparison.INCOMPARABLE);
  }
}
