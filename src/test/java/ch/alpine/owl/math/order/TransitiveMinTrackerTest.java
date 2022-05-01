// code by astoll
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.demo.order.DigitSumDivisibilityPreorder;
import ch.alpine.owl.demo.order.ScalarTotalOrder;
import ch.alpine.sophus.math.TensorShuffle;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class TransitiveMinTrackerTest {
  @Test
  public void testDigestNotEmptyList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = TransitiveMinTracker.of(orderComparator);
    digitSumDivisibility.digest(123);
    assertFalse(digitSumDivisibility.getMinElements().isEmpty());
  }

  @Test
  public void testDigestNotEmptySet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = TransitiveMinTracker.of(orderComparator);
    digitSumDivisibility.digest(123);
    assertFalse(digitSumDivisibility.getMinElements().isEmpty());
  }

  @Test
  public void testPartial() {
    OrderComparator<Scalar> universalComparator = new Order<>(Scalars::divides);
    MinTracker<Scalar> divisibility = TransitiveMinTracker.of(universalComparator);
    divisibility.digest(RealScalar.of(10));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(10)));
    divisibility.digest(RealScalar.of(2));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(2)));
    assertFalse(divisibility.getMinElements().contains(RealScalar.of(10)));
    divisibility.digest(RealScalar.of(3));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(2)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(3)));
    divisibility.digest(RealScalar.of(7));
    divisibility.digest(RealScalar.of(6));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(2)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(3)));
    assertTrue(divisibility.getMinElements().contains(RealScalar.of(7)));
    assertFalse(divisibility.getMinElements().contains(RealScalar.of(6)));
  }

  @Test
  public void testTotal() {
    OrderComparator<Scalar> universalComparator = ScalarTotalOrder.INSTANCE;
    MinTracker<Scalar> lessEquals = TransitiveMinTracker.of(universalComparator);
    lessEquals.digest(RealScalar.of(10));
    assertTrue(lessEquals.getMinElements().contains(RealScalar.of(10)));
    lessEquals.digest(RealScalar.of(2));
    assertTrue(lessEquals.getMinElements().contains(RealScalar.of(2)));
    assertFalse(lessEquals.getMinElements().contains(RealScalar.of(10)));
    lessEquals.digest(RealScalar.of(3));
    assertTrue(lessEquals.getMinElements().contains(RealScalar.of(2)));
    assertFalse(lessEquals.getMinElements().contains(RealScalar.of(3)));
    lessEquals.digest(RealScalar.of(7));
    lessEquals.digest(RealScalar.of(6));
    assertTrue(lessEquals.getMinElements().contains(RealScalar.of(2)));
    assertFalse(lessEquals.getMinElements().contains(RealScalar.of(3)));
    assertFalse(lessEquals.getMinElements().contains(RealScalar.of(7)));
    assertFalse(lessEquals.getMinElements().contains(RealScalar.of(6)));
    assertTrue(lessEquals.getMinElements().size() == 1);
  }

  @Test
  public void testWithList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = TransitiveMinTracker.of(orderComparator);
    digitSumDivisibility.digest(123);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    digitSumDivisibility.digest(122);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    assertTrue(digitSumDivisibility.getMinElements().contains(122));
    digitSumDivisibility.digest(426);
    assertFalse(digitSumDivisibility.getMinElements().contains(426));
    digitSumDivisibility.digest(1);
    assertTrue(digitSumDivisibility.getMinElements().contains(1));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  @Test
  public void testWithSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = TransitiveMinTracker.of(orderComparator);
    digitSumDivisibility.digest(123);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    digitSumDivisibility.digest(122);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    assertTrue(digitSumDivisibility.getMinElements().contains(122));
    digitSumDivisibility.digest(426);
    assertFalse(digitSumDivisibility.getMinElements().contains(426));
    digitSumDivisibility.digest(1);
    assertTrue(digitSumDivisibility.getMinElements().contains(1));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  @Test
  public void testDuplicateEntriesList() throws ClassNotFoundException, IOException {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = //
        Serialization.copy(TransitiveMinTracker.of(orderComparator));
    digitSumDivisibility.digest(333);
    digitSumDivisibility.digest(333);
    assertTrue(digitSumDivisibility.getMinElements().contains(333));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  @Test
  public void testDuplicateEntriesSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = TransitiveMinTracker.of(orderComparator);
    digitSumDivisibility.digest(333);
    digitSumDivisibility.digest(333);
    assertTrue(digitSumDivisibility.getMinElements().contains(333));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  @Test
  public void testLexicographic() throws ClassNotFoundException, IOException {
    List<OrderComparator<? extends Object>> comparators = Collections.nCopies(2, ScalarTotalOrder.INSTANCE);
    Tensor tensorX = Tensors.fromString("{1, 2}");
    Tensor tensorY = Tensors.fromString("{2, 3}");
    LexicographicComparator genericLexicographicOrder = new LexicographicComparator(comparators);
    MinTracker<Iterable<? extends Object>> lexTracker = //
        Serialization.copy(TransitiveMinTracker.of(genericLexicographicOrder));
    lexTracker.digest(tensorX);
    lexTracker.digest(tensorY);
    assertTrue(lexTracker.getMinElements().contains(tensorX));
  }

  private static void _checkPermutations(Supplier<MinTracker<Scalar>> supplier) {
    Distribution distribution = DiscreteUniformDistribution.of(1, 10000);
    final Tensor tensor = RandomVariate.of(distribution, 100);
    Collection<Scalar> collection1;
    {
      MinTracker<Scalar> minTracker = supplier.get();
      TensorShuffle.stream(tensor).map(Scalar.class::cast).forEach(minTracker::digest);
      collection1 = minTracker.getMinElements();
      assertTrue(0 < collection1.size());
    }
    Collection<Scalar> collection2;
    {
      MinTracker<Scalar> minTracker = supplier.get();
      TensorShuffle.stream(tensor).map(Scalar.class::cast).forEach(minTracker::digest);
      collection2 = minTracker.getMinElements();
      assertTrue(0 < collection2.size());
    }
    assertTrue(collection1.containsAll(collection2));
    assertTrue(collection2.containsAll(collection1));
  }

  @Test
  public void testPermutations() {
    _checkPermutations(() -> TransitiveMinTracker.of(DigitSumDivisibilityPreorder.SCALAR));
  }
}
