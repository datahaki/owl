// code by ynager
package ch.alpine.owl.glc.rl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.VectorScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class RLQueueTest {
  @Test
  public void testSimple() {
    Tensor slack = Tensors.vector(1, 0, 0);
    RLQueue rlQueue = new RLQueue(slack);
    GlcNode node21 = GlcNode.of(null, null, VectorScalar.of(2, 1, 2), VectorScalar.of(0, 0, 0));
    GlcNode node12 = GlcNode.of(null, null, VectorScalar.of(1, 2, 1), VectorScalar.of(0, 0, 0));
    GlcNode node22 = GlcNode.of(null, null, VectorScalar.of(2, 2, 2), VectorScalar.of(0, 0, 0));
    GlcNode node23 = GlcNode.of(null, null, VectorScalar.of(2, 3, 2), VectorScalar.of(0, 0, 0));
    GlcNode node32 = GlcNode.of(null, null, VectorScalar.of(3, 2, 3), VectorScalar.of(0, 0, 0));
    GlcNode node31 = GlcNode.of(null, null, VectorScalar.of(3, 1, 3), VectorScalar.of(0, 0, 0));
    // ---
    rlQueue.add(node21);
    rlQueue.add(node12);
    rlQueue.add(node22);
    rlQueue.add(node23);
    rlQueue.add(node32);
    rlQueue.add(node31);
    // ---
    GlcNode best;
    best = rlQueue.poll();
    assertTrue(best.merit() == node21.merit());
    best = rlQueue.poll();
    assertTrue(best.merit() == node12.merit());
    best = rlQueue.poll();
    assertTrue(best.merit() == node31.merit());
    best = rlQueue.poll();
    assertTrue(best.merit() == node22.merit());
    best = rlQueue.poll();
    assertTrue(best.merit() == node32.merit());
    best = rlQueue.poll();
    assertTrue(best.merit() == node23.merit());
    assertTrue(rlQueue.isEmpty());
  }

  @Test
  public void testSpeed() {
    Tensor slack = Tensors.vector(1, 1, 1);
    RLQueue rlQueue = new RLQueue(slack);
    Scalar minCostToGoal = VectorScalar.of(0, 0, 0);
    {
      Distribution distribution = UniformDistribution.of(1, 2);
      Timing timing = Timing.started();
      for (int i = 0; i < 1000; ++i) {
        Scalar costFromRoot = VectorScalar.of(RandomVariate.of(distribution, 3));
        GlcNode node = GlcNode.of(null, null, costFromRoot, minCostToGoal);
        boolean added = rlQueue.add(node);
        assertTrue(added);
      }
      double seconds = timing.seconds(); // 0.045515109000000005
      assertTrue(seconds < 0.1);
    }
    {
      Timing timing = Timing.started();
      rlQueue.poll();
      double seconds = timing.seconds(); // 0.007376575000000001
      assertTrue(seconds < 0.05);
    }
  }

  @Test
  public void testFailCollectionsMinEmpty() {
    assertThrows(Exception.class, () -> Collections.min(Arrays.asList()));
  }
}
