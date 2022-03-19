// code by jph
package ch.alpine.owl.glc.rl2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.demo.order.ScalarTotalOrder;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.SquareMatrixQ;

public class RelaxedPriorityQueueTest {
  @Test
  public void testSTOSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(ScalarTotalOrder.INSTANCE);
  }

  @Test
  public void testPeekNull() throws ClassNotFoundException, IOException {
    RelaxedPriorityQueue relaxedPriorityQueue = Serialization.copy( //
        RelaxedDomainQueue.empty(Tensors.vector(1, 2, 3)));
    assertNull(relaxedPriorityQueue.peekBest());
  }

  @Test
  public void testDimensionsChengQiLu() {
    Tensor matrix = Array.zeros(300, 300);
    List<Integer> list = Dimensions.of(matrix);
    assertEquals(list.get(0), list.get(1));
    SquareMatrixQ.require(matrix);
  }

  @Test
  public void testPollThrows() {
    RelaxedPriorityQueue relaxedPriorityQueue = RelaxedDomainQueue.empty(Tensors.vector(1, 2, 3));
    assertThrows(Exception.class, () -> relaxedPriorityQueue.pollBest());
  }
}
