// code by astoll
package ch.alpine.owl.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.order.MinTracker;
import ch.alpine.owl.math.order.OrderComparator;
import ch.alpine.owl.math.order.OrderComparison;
import ch.alpine.owl.math.order.RepresentativeTransitiveMinTracker;
import ch.alpine.owl.math.order.TransitiveMinTracker;
import ch.alpine.sophus.math.TensorShuffle;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class DigitSumDivisibilityPreorderTest {
  @Test
  void testWithList2() {
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
  void testWithSet2() {
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
  void testDuplicateEntriesList2() throws ClassNotFoundException, IOException {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = //
        Serialization.copy(TransitiveMinTracker.of(orderComparator));
    digitSumDivisibility.digest(333);
    digitSumDivisibility.digest(333);
    assertTrue(digitSumDivisibility.getMinElements().contains(333));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  @Test
  void testDuplicateEntriesSet2() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = TransitiveMinTracker.of(orderComparator);
    digitSumDivisibility.digest(333);
    digitSumDivisibility.digest(333);
    assertTrue(digitSumDivisibility.getMinElements().contains(333));
    assertTrue(digitSumDivisibility.getMinElements().size() == 1);
  }

  @Test
  void testDigestNotEmptyList2() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = TransitiveMinTracker.of(orderComparator);
    digitSumDivisibility.digest(123);
    assertFalse(digitSumDivisibility.getMinElements().isEmpty());
  }

  @Test
  void testDigestNotEmptySet2() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = TransitiveMinTracker.of(orderComparator);
    digitSumDivisibility.digest(123);
    assertFalse(digitSumDivisibility.getMinElements().isEmpty());
  }

  @Test
  void testEquals() {
    OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.INTEGER.compare(321, 6);
    OrderComparison preorderComparison2 = DigitSumDivisibilityPreorder.INTEGER.compare(2, 10001);
    OrderComparison preorderComparison3 = DigitSumDivisibilityPreorder.INTEGER.compare(345, 543);
    OrderComparison preorderComparison4 = DigitSumDivisibilityPreorder.INTEGER.compare(10002, 12);
    assertTrue(preorderComparison1.equals(OrderComparison.INDIFFERENT));
    assertTrue(preorderComparison2.equals(OrderComparison.INDIFFERENT));
    assertTrue(preorderComparison3.equals(OrderComparison.INDIFFERENT));
    assertTrue(preorderComparison4.equals(OrderComparison.INDIFFERENT));
  }

  @Test
  void testEqualsScalar() {
    OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.SCALAR.compare(RealScalar.of(321), RealScalar.of(6));
    assertTrue(preorderComparison1.equals(OrderComparison.INDIFFERENT));
  }

  @Test
  void testGreaterEqualsOnly() {
    OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.INTEGER.compare(372, 6);
    OrderComparison preorderComparison2 = DigitSumDivisibilityPreorder.INTEGER.compare(44, 10001);
    OrderComparison preorderComparison3 = DigitSumDivisibilityPreorder.INTEGER.compare(553434, 543);
    OrderComparison preorderComparison4 = DigitSumDivisibilityPreorder.INTEGER.compare(101892, 1);
    assertTrue(preorderComparison1.equals(OrderComparison.STRICTLY_SUCCEEDS));
    assertTrue(preorderComparison2.equals(OrderComparison.STRICTLY_SUCCEEDS));
    assertTrue(preorderComparison3.equals(OrderComparison.STRICTLY_SUCCEEDS));
    assertTrue(preorderComparison4.equals(OrderComparison.STRICTLY_SUCCEEDS));
  }

  @Test
  void testLessEqualsOnly() {
    OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.INTEGER.compare(1, 6);
    OrderComparison preorderComparison2 = DigitSumDivisibilityPreorder.INTEGER.compare(4, 70001);
    OrderComparison preorderComparison3 = DigitSumDivisibilityPreorder.INTEGER.compare(2, 543);
    OrderComparison preorderComparison4 = DigitSumDivisibilityPreorder.INTEGER.compare(101001, 33333);
    assertTrue(preorderComparison1.equals(OrderComparison.STRICTLY_PRECEDES));
    assertTrue(preorderComparison2.equals(OrderComparison.STRICTLY_PRECEDES));
    assertTrue(preorderComparison3.equals(OrderComparison.STRICTLY_PRECEDES));
    assertTrue(preorderComparison4.equals(OrderComparison.STRICTLY_PRECEDES));
  }

  @Test
  void testIncomparable() {
    OrderComparison preorderComparison1 = DigitSumDivisibilityPreorder.INTEGER.compare(2, 3);
    OrderComparison preorderComparison2 = DigitSumDivisibilityPreorder.INTEGER.compare(4, 80001);
    OrderComparison preorderComparison3 = DigitSumDivisibilityPreorder.INTEGER.compare(2, 533);
    OrderComparison preorderComparison4 = DigitSumDivisibilityPreorder.INTEGER.compare(10001, 333);
    assertTrue(preorderComparison1.equals(OrderComparison.INCOMPARABLE));
    assertTrue(preorderComparison2.equals(OrderComparison.INCOMPARABLE));
    assertTrue(preorderComparison3.equals(OrderComparison.INCOMPARABLE));
    assertTrue(preorderComparison4.equals(OrderComparison.INCOMPARABLE));
  }

  @Test
  void testNegativeAndZeroCase() {
    assertThrows(Exception.class, () -> DigitSumDivisibilityPreorder.INTEGER.compare(0, 3));
    assertThrows(Exception.class, () -> DigitSumDivisibilityPreorder.INTEGER.compare(-3, 3));
  }

  @Test
  void testDigestNotEmptyList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withList(orderComparator);
    minTracker.digest(123);
    assertFalse(minTracker.getMinElements().isEmpty());
  }

  @Test
  void testDigestNotEmptySet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withList(orderComparator);
    minTracker.digest(123);
    assertFalse(minTracker.getMinElements().isEmpty());
  }

  @Test
  void testWithList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withList(orderComparator);
    minTracker.digest(123);
    assertTrue(minTracker.getMinElements().contains(123));
    minTracker.digest(122);
    assertTrue(minTracker.getMinElements().contains(123));
    assertTrue(minTracker.getMinElements().contains(122));
    minTracker.digest(426);
    assertFalse(minTracker.getMinElements().contains(426));
    minTracker.digest(1);
    assertTrue(minTracker.getMinElements().contains(1));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  @Test
  void testWithSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withList(orderComparator);
    digitSumDivisibility.digest(123);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    digitSumDivisibility.digest(122);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    assertTrue(digitSumDivisibility.getMinElements().contains(122));
    digitSumDivisibility.digest(426);
    assertFalse(digitSumDivisibility.getMinElements().contains(426));
    digitSumDivisibility.digest(1);
    assertTrue(digitSumDivisibility.getMinElements().contains(1));
    assertEquals(digitSumDivisibility.getMinElements().size(), 1);
  }

  @Test
  void testDuplicateEntriesList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withList(orderComparator);
    minTracker.digest(333);
    minTracker.digest(333);
    assertTrue(minTracker.getMinElements().contains(333));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  @Test
  void testDuplicateEntriesSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withList(orderComparator);
    minTracker.digest(333);
    minTracker.digest(333);
    assertTrue(minTracker.getMinElements().contains(333));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  @Test
  void testOnlyPreoneRepresentativeList() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> minTracker = RepresentativeTransitiveMinTracker.withList(orderComparator);
    minTracker.digest(123);
    assertTrue(minTracker.getMinElements().contains(123));
    minTracker.digest(213);
    assertFalse(minTracker.getMinElements().contains(213));
    minTracker.digest(443);
    assertTrue(minTracker.getMinElements().contains(443));
    minTracker.digest(1111223);
    assertFalse(minTracker.getMinElements().contains(1111223));
    minTracker.digest(1);
    assertTrue(minTracker.getMinElements().contains(1));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  @Test
  void testOnlyPreoneRepresentativeSet() {
    OrderComparator<Integer> orderComparator = DigitSumDivisibilityPreorder.INTEGER;
    MinTracker<Integer> digitSumDivisibility = RepresentativeTransitiveMinTracker.withList(orderComparator);
    digitSumDivisibility.digest(123);
    assertTrue(digitSumDivisibility.getMinElements().contains(123));
    digitSumDivisibility.digest(213);
    assertFalse(digitSumDivisibility.getMinElements().contains(213));
    digitSumDivisibility.digest(443);
    assertTrue(digitSumDivisibility.getMinElements().contains(443));
    digitSumDivisibility.digest(1111223);
    assertFalse(digitSumDivisibility.getMinElements().contains(1111223));
    digitSumDivisibility.digest(1);
    assertTrue(digitSumDivisibility.getMinElements().contains(1));
    assertEquals(digitSumDivisibility.getMinElements().size(), 1);
  }

  private static void _checkPermutations(Supplier<MinTracker<Scalar>> supplier) {
    Distribution distribution = DiscreteUniformDistribution.of(1, 10000);
    final Tensor tensor = RandomVariate.of(distribution, 100);
    // List<Tensor> list = Unprotect.list(tensor.copy());
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
    assertEquals(collection1.size(), collection2.size());
  }

  @Test
  void testPermutations() {
    _checkPermutations(() -> RepresentativeTransitiveMinTracker.withList(DigitSumDivisibilityPreorder.SCALAR));
  }

  private static void _checkPermutations2(Supplier<MinTracker<Scalar>> supplier) {
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
  void testPermutations2() {
    _checkPermutations2(() -> TransitiveMinTracker.of(DigitSumDivisibilityPreorder.SCALAR));
  }
}
