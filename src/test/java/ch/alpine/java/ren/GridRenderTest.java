// code by jph
package ch.alpine.java.ren;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.mat.HilbertMatrix;

class GridRenderTest {
  @Test
  public void testFailMatrix() {
    assertThrows(Exception.class, () -> new GridRender(HilbertMatrix.of(3), HilbertMatrix.of(4)));
  }

  @Test
  public void testFailScalar() {
    assertThrows(Exception.class, () -> new GridRender(RealScalar.ONE, RealScalar.ZERO));
  }

  @Test
  public void testFailColorNull() {
    assertThrows(Exception.class, () -> new GridRender(Subdivide.of(1, 2, 3), Subdivide.of(1, 2, 3), null));
  }
}
