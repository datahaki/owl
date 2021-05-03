// code by jph
package ch.alpine.owl.math;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class RadiusXYTest extends TestCase {
  public void testSimple() {
    assertEquals(RadiusXY.requireSame(Tensors.vector(2, 2, 3)), RealScalar.of(2));
  }

  public void testFail() {
    AssertFail.of(() -> RadiusXY.requireSame(Tensors.vector(1, 2)));
    AssertFail.of(() -> RadiusXY.requireSame(Tensors.vector(1, 2, 1)));
  }
}
