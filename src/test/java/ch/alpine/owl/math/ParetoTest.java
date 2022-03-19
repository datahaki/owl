// code by ynager
package ch.alpine.owl.math;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class ParetoTest {
  @Test
  public void testSimple() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(2, 2);
    Tensor c = Tensors.vector(1, 2);
    assertTrue(Pareto.isDominated(a, b));
    assertFalse(Pareto.isDominated(b, a));
    assertFalse(Pareto.isDominated(a, c));
  }

  @Test
  public void testFail() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(2, 2, 3);
    AssertFail.of(() -> Pareto.isDominated(a, b));
  }
}
