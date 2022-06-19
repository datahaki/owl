// code by astoll
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.demo.order.ScalarTotalOrder;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Abs;

class LexicographicOrderTest {
  @Test
  void testTotalLexciographic() {
    OrderComparator<Scalar> comparator1 = ScalarTotalOrder.INSTANCE;
    List<OrderComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    LexicographicOrder<Scalar> lexicographicOrder = new LexicographicOrder<>(comparatorList);
    List<Scalar> x = new LinkedList<>();
    x.add(RealScalar.of(1));
    x.add(RealScalar.of(2));
    x.add(RealScalar.of(2));
    List<Scalar> y = new LinkedList<>();
    y.add(RealScalar.of(2));
    y.add(RealScalar.of(2));
    y.add(RealScalar.of(2));
    assertEquals(OrderComparison.STRICTLY_PRECEDES, lexicographicOrder.compare(x, y));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, lexicographicOrder.compare(y, x));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
  }

  @Test
  void testPartialLexicographic() {
    OrderComparator<Scalar> comparator1 = new Order<>((x, y) -> Scalars.divides(Abs.of(x), Abs.of(y)));
    List<OrderComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    LexicographicOrder<Scalar> lexicographicOrder = new LexicographicOrder<>(comparatorList);
    List<Scalar> x = new LinkedList<>();
    x.add(RealScalar.of(2));
    x.add(RealScalar.of(-5));
    x.add(RealScalar.of(2));
    List<Scalar> y = new LinkedList<>();
    y.add(RealScalar.of(6));
    y.add(RealScalar.of(2));
    y.add(RealScalar.of(2));
    List<Scalar> z = new LinkedList<>();
    z.add(RealScalar.of(7));
    z.add(RealScalar.of(2));
    z.add(RealScalar.of(2));
    assertEquals(OrderComparison.STRICTLY_PRECEDES, lexicographicOrder.compare(x, y));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, lexicographicOrder.compare(y, x));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
    assertEquals(OrderComparison.INCOMPARABLE, lexicographicOrder.compare(x, z));
    assertEquals(OrderComparison.INCOMPARABLE, lexicographicOrder.compare(y, z));
    assertEquals(OrderComparison.INCOMPARABLE, lexicographicOrder.compare(z, x));
    assertEquals(OrderComparison.INCOMPARABLE, lexicographicOrder.compare(z, y));
  }

  @Test
  void testException() {
    OrderComparator<Scalar> comparator1 = new Order<>((x, y) -> Scalars.divides(Abs.of(x), Abs.of(y)));
    List<OrderComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    LexicographicOrder<Scalar> lexicographicOrder = new LexicographicOrder<>(comparatorList);
    List<Scalar> x = Tensors.vector(2, -5, 2).stream().map(Scalar.class::cast).collect(Collectors.toList());
    List<Scalar> y = Tensors.vector(6, 2).stream().map(Scalar.class::cast).collect(Collectors.toList());
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
    assertThrows(Exception.class, () -> lexicographicOrder.compare(x, y));
    assertThrows(Exception.class, () -> lexicographicOrder.compare(y, x));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    @SuppressWarnings("unchecked")
    OrderComparator<Scalar> comparator1 = new Order<>( //
        (BiPredicate<Scalar, Scalar> & Serializable) (x, y) -> Scalars.divides(Abs.of(x), Abs.of(y)));
    List<OrderComparator<Scalar>> comparatorList = new LinkedList<>();
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    comparatorList.add(comparator1);
    LexicographicOrder<Scalar> lexicographicOrder = Serialization.copy( //
        new LexicographicOrder<>(comparatorList));
    List<Scalar> x = Tensors.vector(2, -5, 2).stream().map(Scalar.class::cast).collect(Collectors.toList());
    List<Scalar> y = Tensors.vector(6, 2).stream().map(Scalar.class::cast).collect(Collectors.toList());
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(x, x));
    assertEquals(OrderComparison.INDIFFERENT, lexicographicOrder.compare(y, y));
  }

  @Test
  void testEmpty() {
    LexicographicOrder<Object> lexicographicOrder = new LexicographicOrder<>(Collections.emptyList());
    OrderComparison orderComparison = lexicographicOrder.compare(Collections.emptyList(), Collections.emptyList());
    assertEquals(orderComparison, OrderComparison.INDIFFERENT);
  }
}
