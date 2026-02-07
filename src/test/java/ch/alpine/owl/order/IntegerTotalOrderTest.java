package ch.alpine.owl.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.order.LexicographicComparator;
import ch.alpine.owl.math.order.OrderComparator;
import ch.alpine.owl.math.order.OrderComparison;
import ch.alpine.owl.math.order.ProductOrderComparator;
import ch.alpine.owl.math.order.ScalarTotalOrder;
import ch.alpine.owl.math.order.SetPartialOrder;
import ch.alpine.tensor.RealScalar;

class IntegerTotalOrderTest {
  @Test
  void testMixed2() {
    List<OrderComparator<? extends Object>> comparators = Arrays.asList( //
        IntegerTotalOrder.INSTANCE, //
        SetPartialOrder.INSTANCE); //
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    List<Object> listX = Arrays.asList(123, Arrays.asList(2, 3, 4));
    List<Object> listY = Arrays.asList(123, Arrays.asList(3, 4));
    assertEquals(genericLexicographicOrder.compare(listX, listY), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(genericLexicographicOrder.compare(listY, listX), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(genericLexicographicOrder.compare(listX, listX), OrderComparison.INDIFFERENT);
    assertEquals(genericLexicographicOrder.compare(listY, listY), OrderComparison.INDIFFERENT);
  }

  @Test
  void testEquality() {
    List<OrderComparator<? extends Object>> comparators = Arrays.asList( //
        IntegerTotalOrder.INSTANCE, //
        ScalarTotalOrder.INSTANCE, //
        EqualityOrder.INSTANCE); //
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    List<Object> listX = Arrays.asList(2, RealScalar.of(3), "abc");
    List<Object> listY = Arrays.asList(2, RealScalar.of(3), "different");
    assertEquals(genericLexicographicOrder.compare(listX, listY), OrderComparison.INCOMPARABLE);
    assertEquals(genericLexicographicOrder.compare(listY, listX), OrderComparison.INCOMPARABLE);
    assertEquals(genericLexicographicOrder.compare(listX, listX), OrderComparison.INDIFFERENT);
    assertEquals(genericLexicographicOrder.compare(listY, listY), OrderComparison.INDIFFERENT);
  }

  @Test
  void testMixed() {
    List<OrderComparator<? extends Object>> comparators = Arrays.asList( //
        IntegerTotalOrder.INSTANCE, //
        SetPartialOrder.INSTANCE, //
        EqualityOrder.INSTANCE); //
    ProductOrderComparator productOrderComparator = new ProductOrderComparator(comparators);
    List<Object> listX = Arrays.asList(123, Arrays.asList(2, 3, 4), "abc");
    List<Object> listY = Arrays.asList(123, Arrays.asList(3, 4), "abc");
    List<Object> listZ = Arrays.asList(123, Arrays.asList(3, 4), "different");
    assertEquals(productOrderComparator.compare(listX, listY), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(productOrderComparator.compare(listY, listX), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(productOrderComparator.compare(listX, listX), OrderComparison.INDIFFERENT);
    assertEquals(productOrderComparator.compare(listY, listY), OrderComparison.INDIFFERENT);
    assertEquals(productOrderComparator.compare(listX, listZ), OrderComparison.INCOMPARABLE);
    assertEquals(productOrderComparator.compare(listY, listZ), OrderComparison.INCOMPARABLE);
    assertEquals(productOrderComparator.compare(listZ, listX), OrderComparison.INCOMPARABLE);
    assertEquals(productOrderComparator.compare(listZ, listY), OrderComparison.INCOMPARABLE);
    assertEquals(productOrderComparator.compare(listZ, listZ), OrderComparison.INDIFFERENT);
  }
}
