// code by jph
package ch.alpine.owl.demo.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.order.OrderComparator;
import ch.alpine.owl.math.order.OrderComparison;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.MatrixInfinityNorm;
import ch.alpine.tensor.nrm.VectorInfinityNorm;

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
}
