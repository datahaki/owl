// code by astoll
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.demo.order.EqualityOrder;
import ch.alpine.owl.demo.order.IntegerTotalOrder;
import ch.alpine.owl.demo.order.ScalarTotalOrder;
import ch.alpine.owl.demo.order.SetPartialOrder;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class ProductOrderComparatorTest {
  @Test
  void testSimple() {
    List<OrderComparator<? extends Object>> comparators = Arrays.asList( //
        ScalarTotalOrder.INSTANCE, //
        ScalarTotalOrder.INSTANCE); //
    ProductOrderComparator productOrderComparator = new ProductOrderComparator(comparators);
    List<Scalar> list = Arrays.asList(RealScalar.ONE, RealScalar.of(3));
    OrderComparison orderComparison = productOrderComparator.compare(list, list);
    assertEquals(orderComparison, OrderComparison.INDIFFERENT);
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

  @Test
  void testTensor() {
    BinaryRelation<Tensor> relation1 = (x, y) -> x.length() <= y.length();
    List<OrderComparator<? extends Object>> comparators = Arrays.asList( //
        new Order<>(relation1), //
        ScalarTotalOrder.INSTANCE); //
    ProductOrderComparator genericProductOrder = new ProductOrderComparator(comparators);
    Tensor tensorX = Tensors.fromString("{{1, 2, 3}, 10}");
    Tensor tensorY = Tensors.fromString("{{2, 3, 4, 5}, 7}");
    OrderComparison orderComparison = genericProductOrder.compare(tensorX, tensorY);
    assertEquals(orderComparison, OrderComparison.INCOMPARABLE);
  }
}
