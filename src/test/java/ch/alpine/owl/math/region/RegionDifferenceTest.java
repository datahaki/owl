// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class RegionDifferenceTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = RegionDifference.of( //
        new BallRegion(Tensors.vector(0, 0), RealScalar.ONE), //
        new BallRegion(Tensors.vector(1, 0), RealScalar.ONE));
    assertTrue(region.isMember(Tensors.vector(-0.5, 0)));
    assertFalse(region.isMember(Tensors.vector(0.5, 0)));
    assertFalse(region.isMember(Tensors.vector(2.5, 0)));
  }
}
