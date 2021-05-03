// code by jph
package ch.alpine.owl.glc.rl2;

import java.io.IOException;
import java.util.List;

import ch.alpine.owl.demo.order.ScalarTotalOrder;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.SquareMatrixQ;
import junit.framework.TestCase;

public class RelaxedPriorityQueueTest extends TestCase {
  public void testSTOSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(ScalarTotalOrder.INSTANCE);
  }

  public void testPeekNull() throws ClassNotFoundException, IOException {
    RelaxedPriorityQueue relaxedPriorityQueue = Serialization.copy( //
        RelaxedDomainQueue.empty(Tensors.vector(1, 2, 3)));
    assertNull(relaxedPriorityQueue.peekBest());
  }

  public void testDimensionsChengQiLu() {
    Tensor matrix = Array.zeros(300, 300);
    List<Integer> list = Dimensions.of(matrix);
    assertEquals(list.get(0), list.get(1));
    SquareMatrixQ.require(matrix);
  }

  public void testPollThrows() {
    RelaxedPriorityQueue relaxedPriorityQueue = RelaxedDomainQueue.empty(Tensors.vector(1, 2, 3));
    AssertFail.of(() -> relaxedPriorityQueue.pollBest());
  }
}
