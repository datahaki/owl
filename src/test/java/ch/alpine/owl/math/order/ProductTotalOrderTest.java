// code by astoll
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class ProductTotalOrderTest {
  @SuppressWarnings("rawtypes")
  @Test
  public void testEquals() {
    List<Comparable> x = Arrays.asList(1, "zwei");
    List<Comparable> y = Arrays.asList(1, "zwei");
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(OrderComparison.INDIFFERENT));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(OrderComparison.INDIFFERENT));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testIncomparable() {
    List<Comparable> x = Arrays.asList(true, 1.34);
    List<Comparable> y = Arrays.asList(false, 3.56);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(OrderComparison.INCOMPARABLE));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(OrderComparison.INCOMPARABLE));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testIncomparable3() {
    List<Comparable> x = Arrays.asList(2, true, 1.34);
    List<Comparable> y = Arrays.asList(2, false, 3.56);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(OrderComparison.INCOMPARABLE));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(OrderComparison.INCOMPARABLE));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testLessThan() {
    List<Comparable> x = Arrays.asList(false, 1.34, 2);
    List<Comparable> y = Arrays.asList(true, 3.56, 2);
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(OrderComparison.STRICTLY_PRECEDES));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(OrderComparison.STRICTLY_SUCCEEDS));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testGreaterThan() {
    List<Comparable> x = Arrays.asList("zwei", 'a');
    List<Comparable> y = Arrays.asList("drei", 'a');
    assertTrue(ProductTotalOrder.INSTANCE.compare(x, y).equals(OrderComparison.STRICTLY_SUCCEEDS));
    assertTrue(ProductTotalOrder.INSTANCE.compare(y, x).equals(OrderComparison.STRICTLY_PRECEDES));
  }

  @Test
  public void testEmpty() {
    OrderComparison orderComparison = ProductTotalOrder.INSTANCE.compare(Arrays.asList(), Arrays.asList());
    assertEquals(orderComparison, OrderComparison.INDIFFERENT);
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testSizeException() {
    List<Comparable> x = Arrays.asList("zwei", 'a');
    List<Comparable> y = Arrays.asList("drei");
    assertThrows(Exception.class, () -> ProductTotalOrder.INSTANCE.compare(x, y));
    assertThrows(Exception.class, () -> ProductTotalOrder.INSTANCE.compare(y, x));
  }

  @Test
  public void testNullException() {
    assertThrows(Exception.class, () -> ProductTotalOrder.INSTANCE.compare(Arrays.asList(2), null));
    assertThrows(Exception.class, () -> ProductTotalOrder.INSTANCE.compare(null, Arrays.asList(2)));
  }
}
