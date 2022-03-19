// code by jph
package ch.alpine.owl.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

public class RadiusXYTest {
  @Test
  public void testSimple() {
    assertEquals(RadiusXY.requireSame(Tensors.vector(2, 2, 3)), RealScalar.of(2));
  }

  @Test
  public void testFail() {
    assertThrows(Exception.class, () -> RadiusXY.requireSame(Tensors.vector(1, 2)));
    assertThrows(Exception.class, () -> RadiusXY.requireSame(Tensors.vector(1, 2, 1)));
  }
}
