// code by jph
package ch.alpine.java.ren;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class GridRenderTest extends TestCase {
  public void testFailMatrix() {
    AssertFail.of(() -> new GridRender(HilbertMatrix.of(3), HilbertMatrix.of(4)));
  }

  public void testFailScalar() {
    AssertFail.of(() -> new GridRender(RealScalar.ONE, RealScalar.ZERO));
  }

  public void testFailColorNull() {
    AssertFail.of(() -> new GridRender(Subdivide.of(1, 2, 3), Subdivide.of(1, 2, 3), null));
  }
}
