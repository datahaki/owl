// code by jph
package ch.alpine.java.ren;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.mat.HilbertMatrix;

public class GridRenderTest {
  @Test
  public void testFailMatrix() {
    AssertFail.of(() -> new GridRender(HilbertMatrix.of(3), HilbertMatrix.of(4)));
  }

  @Test
  public void testFailScalar() {
    AssertFail.of(() -> new GridRender(RealScalar.ONE, RealScalar.ZERO));
  }

  @Test
  public void testFailColorNull() {
    AssertFail.of(() -> new GridRender(Subdivide.of(1, 2, 3), Subdivide.of(1, 2, 3), null));
  }
}
