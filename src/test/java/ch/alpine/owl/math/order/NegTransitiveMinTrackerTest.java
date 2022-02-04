// code by astoll
package ch.alpine.owl.math.order;

import java.io.IOException;
import java.util.Collection;

import ch.alpine.owl.demo.order.TensorNormTotalPreorder;
import ch.alpine.sophus.math.TensorShuffle;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.VectorInfinityNorm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import junit.framework.TestCase;

public class NegTransitiveMinTrackerTest extends TestCase {
  public void testDigestNotEmpty() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = NegTransitiveMinTracker.of(weakOrderComparator);
    minTracker.digest(RealScalar.of(6));
    assertEquals(minTracker.getMinElements().size(), 1);
  }

  public void testDigestFunction() {
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

  public void testDuplicateEntries() {
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

  public void testWithSet() {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = NegTransitiveMinTracker.of(weakOrderComparator);
    minTracker.digest(Tensors.vector(0, 1, 2));
    minTracker.digest(Tensors.vector(0, 1, 2));
    assertEquals(minTracker.getMinElements().size(), 1);
    minTracker.digest(Tensors.vector(0, 2, 1));
    assertEquals(minTracker.getMinElements().size(), 2);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    TensorNormTotalPreorder tensorNormWeakOrder = new TensorNormTotalPreorder(VectorInfinityNorm::of);
    OrderComparator<Tensor> weakOrderComparator = tensorNormWeakOrder.comparator();
    MinTracker<Tensor> minTracker = Serialization.copy(NegTransitiveMinTracker.of(weakOrderComparator));
    minTracker.digest(Tensors.vector(0, 1, 2));
  }

  public void testPermutations() {
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
