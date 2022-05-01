// code by jph
package ch.alpine.owl.bot.rn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.math.region.BufferedImageRegion;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;

class RnPointcloudRegionsTest {
  @Test
  public void testSimple2D() {
    Region<Tensor> region = RnPointcloudRegions.createRandomRegion(1, Tensors.vector(10, 10), Tensors.vector(1, 1), RealScalar.of(1.5));
    assertTrue(region.test(Tensors.vector(10.5, 10.5)));
    assertTrue(region.test(Tensors.vector(10, 10)));
    assertFalse(region.test(Tensors.vector(8, 8)));
    assertFalse(region.test(Tensors.vector(13, 13)));
  }

  @Test
  public void testPointsMatrix() {
    Region<Tensor> imageRegion = ImageRegions.loadFromRepository( //
        "/io/track0_100.png", Tensors.vector(10, 10), false);
    Tensor points = RnPointcloudRegions.points((BufferedImageRegion) imageRegion);
    assertEquals(Dimensions.of(points), Arrays.asList(1429, 2));
  }
}
