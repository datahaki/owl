// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.io.IOException;
import java.util.PriorityQueue;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testIsSimilar() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    RelaxedPriorityQueue rlQueue = RelaxedDomainQueue.singleton(node1, slacks);
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2.1), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(1, 1, 1.9), VectorScalar.of(0, 0, 0));
    assertFalse(StaticHelper.isSimilar(node2, rlQueue));
    assertFalse(StaticHelper.isSimilar(node3, rlQueue));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(1.5, 0.5, 1.9), VectorScalar.of(0, 0, 0));
    assertFalse(StaticHelper.isSimilar(node4, rlQueue));
    rlQueue.add(node4);
    assertTrue(rlQueue.collection().contains(node4));
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(1.6, 0.5, 2), VectorScalar.of(0, 0, 0));
    GlcNode node6 = GlcNode.of(null, null, VectorScalar.of(1.6, 0, 1.9), VectorScalar.of(0, 0, 0));
    assertFalse(StaticHelper.isSimilar(node5, rlQueue));
    assertFalse(StaticHelper.isSimilar(node6, rlQueue));
  }

  public void testNumberEquals() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    GlcNode node1 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    RelaxedPriorityQueue rlQueue = RelaxedDomainQueue.singleton(node1, slacks);
    GlcNode node2 = GlcNode.of(null, null, VectorScalar.of(1, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node3 = GlcNode.of(null, null, VectorScalar.of(1, 1, 1.999999999), VectorScalar.of(0, 0, 0));
    GlcNode node4 = GlcNode.of(null, null, VectorScalar.of(1.5, 0.5, 1.9), VectorScalar.of(0, 0, 0));
    rlQueue.add(node2);
    rlQueue.add(node3);
    rlQueue.add(node4);
    assertEquals(StaticHelper.numberEquals(rlQueue), 1);
    GlcNode node5 = GlcNode.of(null, null, VectorScalar.of(0, 0.5, 2), VectorScalar.of(0, 0, 0));
    GlcNode node6 = GlcNode.of(null, null, VectorScalar.of(0, 0.4999, 1.9), VectorScalar.of(0, 0, 0));
    rlQueue.add(node5);
    rlQueue.add(node6);
    assertEquals(StaticHelper.numberEquals(rlQueue), 1);
  }

  public void testPriorityQueue() throws ClassNotFoundException, IOException {
    PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();
    priorityQueue.add(2);
    priorityQueue.add(1);
    priorityQueue.add(3);
    PriorityQueue<Integer> copy = Serialization.copy(priorityQueue);
    assertEquals(copy.size(), 3);
  }
}
