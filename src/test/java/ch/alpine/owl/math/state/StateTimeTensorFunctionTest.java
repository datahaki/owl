// code by jph
package ch.alpine.owl.math.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class StateTimeTensorFunctionTest {
  @Test
  public void testWithTime() {
    Function<StateTime, Tensor> sttf = StateTime::joined;
    Tensor key = sttf.apply(new StateTime(Tensors.vector(1, 2), RealScalar.of(3)));
    assertEquals(key, Tensors.vector(1, 2, 3));
  }

  @Test
  public void testState() {
    Tensor key = StateTimeTensorFunction.state(tensor -> tensor.multiply(RealScalar.of(5))) //
        .apply(new StateTime(Tensors.vector(1, 2), RealScalar.of(3)));
    assertEquals(key, Tensors.vector(5, 10));
  }
}
