// code by ynager
package ch.alpine.owl.math;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class ParetoTest {
  @Test
  void testSimple() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(2, 2);
    Tensor c = Tensors.vector(1, 2);
    assertTrue(Pareto.isDominated(a, b));
    assertFalse(Pareto.isDominated(b, a));
    assertFalse(Pareto.isDominated(a, c));
  }

  @Test
  void testFail() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(2, 2, 3);
    assertThrows(Exception.class, () -> Pareto.isDominated(a, b));
  }
}
