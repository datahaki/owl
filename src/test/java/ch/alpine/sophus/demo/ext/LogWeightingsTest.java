// code by jph
package ch.alpine.sophus.demo.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;

class Some implements TensorUnaryOperator {
  public Some(List<Integer> list) {
    list.add(0);
  }

  @Override
  public Tensor apply(Tensor t) {
    return t;
  }
}

public class LogWeightingsTest {
  @Test
  public void testDoubleColon() {
    List<Integer> list = new ArrayList<>();
    TensorUnaryOperator s = new Some(list)::apply;
    s.apply(Tensors.vector(1, 2, 3));
    s.apply(Tensors.empty());
    assertEquals(list.size(), 1);
  }

  @Test
  public void testArrow() {
    List<Integer> list = new ArrayList<>();
    TensorUnaryOperator s = t -> new Some(list).apply(t);
    s.apply(Tensors.vector(1, 2, 3));
    s.apply(Tensors.empty());
    assertEquals(list.size(), 2);
  }
}
