// code by astoll, jph
package ch.alpine.owl.math.order;

import java.util.LinkedList;
import java.util.List;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class TensorProductOrderTest extends TestCase {
  private static void _check(Tensor x, Tensor y, OrderComparison orderComparison) {
    ProductOrderComparator productOrderComparator = TensorProductOrder.comparator(x.length());
    assertEquals(productOrderComparator.compare(x, y), orderComparison);
    assertEquals(productOrderComparator.compare(y, x), InverseOrderComparison.of(orderComparison));
    assertEquals(productOrderComparator.compare(x, x), OrderComparison.INDIFFERENT);
    assertEquals(productOrderComparator.compare(y, y), OrderComparison.INDIFFERENT);
  }

  public void testSimple() {
    Tensor tensorX = Tensors.fromString("{1, 2, 3}");
    Tensor tensorY = Tensors.fromString("{2, 3, 4}");
    Tensor tensorA = Tensors.fromString("{0, 3, 3}");
    Tensor tensorZ = Tensors.fromString("{0, 3, 4}");
    _check(tensorX, tensorY, OrderComparison.STRICTLY_PRECEDES);
    _check(tensorY, tensorX, OrderComparison.STRICTLY_SUCCEEDS);
    _check(tensorX, tensorZ, OrderComparison.INCOMPARABLE);
    _check(tensorX, tensorA, OrderComparison.INCOMPARABLE);
    _check(tensorA, tensorX, OrderComparison.INCOMPARABLE);
  }

  public void testDimOne() {
    ProductOrderComparator productOrderComparator = TensorProductOrder.comparator(2);
    Tensor tensorX = Tensors.fromString("{1, 2, 3}");
    Tensor tensorY = Tensors.fromString("{2, 3, 4}");
    OrderComparison orderComparison1 = productOrderComparator.compare(tensorX.extract(0, 2), tensorY.extract(0, 2));
    assertEquals(orderComparison1, OrderComparison.STRICTLY_PRECEDES);
  }

  public void testTotalProduct() {
    ProductOrderComparator productOrderComparator = TensorProductOrder.comparator(3);
    List<Scalar> x = new LinkedList<>();
    x.add(RealScalar.of(1));
    x.add(RealScalar.of(2));
    x.add(RealScalar.of(2));
    List<Scalar> y = new LinkedList<>();
    y.add(RealScalar.of(2));
    y.add(RealScalar.of(3));
    y.add(RealScalar.of(3));
    List<Scalar> z = new LinkedList<>();
    z.add(RealScalar.of(2));
    z.add(RealScalar.of(2));
    z.add(RealScalar.of(2));
    assertEquals(OrderComparison.STRICTLY_PRECEDES, productOrderComparator.compare(x, y));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, productOrderComparator.compare(y, x));
    assertEquals(OrderComparison.STRICTLY_SUCCEEDS, productOrderComparator.compare(y, z));
    assertEquals(OrderComparison.STRICTLY_PRECEDES, productOrderComparator.compare(x, z));
    assertEquals(OrderComparison.INDIFFERENT, productOrderComparator.compare(x, x));
    assertEquals(OrderComparison.INDIFFERENT, productOrderComparator.compare(y, y));
    assertEquals(OrderComparison.INDIFFERENT, productOrderComparator.compare(z, z));
  }

  public void testFailLength() {
    ProductOrderComparator productOrderComparator = TensorProductOrder.comparator(3);
    Tensor tensorX = Tensors.fromString("{1, 2}");
    Tensor tensorY = Tensors.fromString("{2, 3}");
    AssertFail.of(() -> productOrderComparator.compare(tensorX, tensorY));
  }

  public void testZero() {
    assertEquals(TensorProductOrder.comparator(0).compare(Tensors.empty(), Tensors.empty()), OrderComparison.INDIFFERENT);
  }

  public void testFailNegative() {
    AssertFail.of(() -> TensorProductOrder.comparator(-1));
  }
}
