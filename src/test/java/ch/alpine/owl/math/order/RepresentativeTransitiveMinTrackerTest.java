// code by astoll
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.demo.order.DigitSumDivisibilityPreorder;
import ch.alpine.sophus.math.TensorShuffle;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class RepresentativeTransitiveMinTrackerTest {
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
}
