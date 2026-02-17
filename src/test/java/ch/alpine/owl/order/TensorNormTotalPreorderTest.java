// code by jph
package ch.alpine.owl.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.order.MinTracker;
import ch.alpine.owl.math.order.NegTransitiveMinTracker;
import ch.alpine.owl.math.order.OrderComparator;
import ch.alpine.owl.math.order.OrderComparison;
import ch.alpine.owl.math.order.RepresentativeNegTransitiveMinTracker;
import ch.alpine.sophus.math.TensorShuffle;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.MatrixInfinityNorm;
import ch.alpine.tensor.nrm.VectorInfinityNorm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class TensorNormTotalPreorderTest {
  @Test
  void testSimple() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    assertEquals(weakOrderComparator.compare(Tensors.vector(1, 3), Tensors.vector(3, 12)), OrderComparison.STRICTLY_PRECEDES);
    assertEquals(weakOrderComparator.compare(Tensors.vector(12, 3), Tensors.vector(3, 12)), OrderComparison.INDIFFERENT);
    assertEquals(weakOrderComparator.compare(Tensors.vector(3, 12), Tensors.vector(3, 12)), OrderComparison.INDIFFERENT);
    assertEquals(weakOrderComparator.compare(Tensors.vector(100, 3), Tensors.vector(3, 12)), OrderComparison.STRICTLY_SUCCEEDS);
  }

  @Test
  void testVector() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparison weakOrderComparison = tensorNormWeakOrder.comparator().compare(Tensors.vector(0, 1, 2), Tensors.vector(0, 2, 1));
    assertEquals(weakOrderComparison, OrderComparison.INDIFFERENT);
  }

  @Test
  void testMatrix() throws ClassNotFoundException, IOException {
    TensorNormTotalPreorder tensorNormWeakOrder = //
        Serialization.copy(new TensorNormTotalPreorder(MatrixInfinityNorm::of));
    Tensor m1 = Tensors.fromString("{{1, 2}, {2, 3}}");
    Tensor m2 = Tensors.fromString("{{2, 1}, {2, 3}}");
    Tensor m3 = Tensors.fromString("{{1, 1}, {2, 3}}");
    Tensor m4 = Tensors.fromString("{{1, 1}, {1, 3}}");
    assertEquals(tensorNormWeakOrder.comparator().compare(m1, m2), OrderComparison.INDIFFERENT);
    assertEquals(tensorNormWeakOrder.comparator().compare(m1, m3), OrderComparison.INDIFFERENT);
    assertEquals(tensorNormWeakOrder.comparator().compare(m2, m3), OrderComparison.INDIFFERENT);
    assertEquals(tensorNormWeakOrder.comparator().compare(m1, m4), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(tensorNormWeakOrder.comparator().compare(m2, m4), OrderComparison.STRICTLY_SUCCEEDS);
    assertEquals(tensorNormWeakOrder.comparator().compare(m3, m4), OrderComparison.STRICTLY_SUCCEEDS);
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> new TensorNormTotalPreorder(null));
  }

  @Test
  void testDigestNotEmpty() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = RepresentativeNegTransitiveMinTracker.withList(weakOrderComparator);
    minTracker.digest(RealScalar.of(6));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  @Test
  void testDigestFunction() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = RepresentativeNegTransitiveMinTracker.withList(weakOrderComparator);
    minTracker.digest(Tensors.vector(2));
    minTracker.digest(Tensors.vector(0, 3, 2));
    assertEquals(minTracker.getMinElements().size(), 1);
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(2)));
    minTracker.digest(Tensors.vector(0, 2, 2));
    assertFalse(minTracker.getMinElements().contains(Tensors.vector(0, 2, 2)));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertFalse(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    minTracker.digest(Tensors.vector(0, 3, 2));
    assertFalse(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertFalse(minTracker.getMinElements().contains(Tensors.vector(0, 3, 2)));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  @Test
  void testDuplicateEntries() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = RepresentativeNegTransitiveMinTracker.withList(weakOrderComparator);
    minTracker.digest(Tensors.vector(0, 1, 2));
    minTracker.digest(Tensors.vector(0, 4, 1));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertEquals(minTracker.getMinElements().size(), 1);
    minTracker.digest(Tensors.vector(0, 1, 1));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 1)));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  @Test
  void testWithSet() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = RepresentativeNegTransitiveMinTracker.withList(weakOrderComparator);
    minTracker.digest(Tensors.vector(0, 0, 2));
    minTracker.digest(Tensors.vector(0, 0, 2));
    assertEquals(minTracker.getMinElements().size(), 1);
    minTracker.digest(Tensors.vector(0, 2, 0));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = Serialization.copy(RepresentativeNegTransitiveMinTracker.withList(weakOrderComparator));
    minTracker.digest(Tensors.vector(0, 1, 2));
  }

  @Test
  void testIncomparability() {
    OrderComparator<Clip> intervalOrder = ClipStrictPartialOrder.INSTANCE;
    MinTracker<Clip> minTracker = RepresentativeNegTransitiveMinTracker.withList(intervalOrder);
    minTracker.digest(Clips.interval(0, 1));
    minTracker.digest(Clips.interval(0, 1));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  @Test
  void testDigestNotEmpty2() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = NegTransitiveMinTracker.of(weakOrderComparator);
    minTracker.digest(RealScalar.of(6));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  @Test
  void testDigestFunction2() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = NegTransitiveMinTracker.of(weakOrderComparator);
    minTracker.digest(Tensors.vector(2));
    minTracker.digest(Tensors.vector(0, 3, 2));
    assertEquals(minTracker.getMinElements().size(), 1);
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(2)));
    minTracker.digest(Tensors.vector(0, 2, 2));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 2, 2)));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    minTracker.digest(Tensors.vector(0, 3, 2));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertFalse(minTracker.getMinElements().contains(Tensors.vector(0, 3, 2)));
    assertEquals(minTracker.getMinElements().size(), 3);
  }

  @Test
  void testDuplicateEntries2() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = NegTransitiveMinTracker.of(weakOrderComparator);
    minTracker.digest(Tensors.vector(0, 1, 2));
    minTracker.digest(Tensors.vector(0, 4, 1));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    assertEquals(minTracker.getMinElements().size(), 1);
    minTracker.digest(Tensors.vector(0, 2, 1));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 1, 2)));
    minTracker.digest(Tensors.vector(0, 2, 1));
    minTracker.digest(Tensors.vector(0, 1, 2));
    minTracker.digest(Tensors.vector(0, 3, 3));
    assertTrue(minTracker.getMinElements().contains(Tensors.vector(0, 2, 1)));
    assertEquals(minTracker.getMinElements().size(), 2);
  }

  @Test
  void testWithSet2() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = NegTransitiveMinTracker.of(weakOrderComparator);
    minTracker.digest(Tensors.vector(0, 1, 2));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertEquals(minTracker.getMinElements().size(), 1);
    minTracker.digest(Tensors.vector(0, 2, 1));
    assertEquals(minTracker.getMinElements().size(), 2);
  }

  @Test
  void testSerializable2() throws ClassNotFoundException, IOException {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = Serialization.copy(NegTransitiveMinTracker.of(weakOrderComparator));
    minTracker.digest(Tensors.vector(0, 1, 2));
  }

  @Test
  void testPermutations() {
    Distribution distribution = DiscreteUniformDistribution.of(0, 10);
    final Tensor tensor = RandomVariate.of(distribution, 100, 3);
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    Collection<Tensor> collection1;
    {
      MinTracker<Tensor> minTracker = NegTransitiveMinTracker.of(weakOrderComparator);
      TensorShuffle.stream(tensor).forEach(minTracker::digest);
      collection1 = minTracker.getMinElements();
      assertTrue(0 < collection1.size());
    }
    Collection<Tensor> collection2;
    {
      MinTracker<Tensor> minTracker = NegTransitiveMinTracker.of(weakOrderComparator);
      TensorShuffle.stream(tensor).forEach(minTracker::digest);
      collection2 = minTracker.getMinElements();
      assertTrue(0 < collection2.size());
    }
    assertTrue(collection1.containsAll(collection2));
    assertTrue(collection2.containsAll(collection1));
  }
}
