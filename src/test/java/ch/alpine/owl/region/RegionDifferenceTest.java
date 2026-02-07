// code by jph
package ch.alpine.owl.region;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class RegionDifferenceTest {
  @Test
  void testSimple() {
    Region<Tensor> region = RegionDifference.of( //
        new BallRegion(Tensors.vector(0, 0), RealScalar.ONE), //
        new BallRegion(Tensors.vector(1, 0), RealScalar.ONE));
    assertTrue(region.test(Tensors.vector(-0.5, 0)));
    assertFalse(region.test(Tensors.vector(0.5, 0)));
    assertFalse(region.test(Tensors.vector(2.5, 0)));
  }
}
