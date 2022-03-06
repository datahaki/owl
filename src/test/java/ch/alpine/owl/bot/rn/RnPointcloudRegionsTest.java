// code by jph
package ch.alpine.owl.bot.rn;

import java.util.Arrays;

import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.math.region.BufferedImageRegion;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class RnPointcloudRegionsTest extends TestCase {
  public void testSimple2D() {
    Region<Tensor> region = RnPointcloudRegions.createRandomRegion(1, Tensors.vector(10, 10), Tensors.vector(1, 1), RealScalar.of(1.5));
    assertTrue(region.test(Tensors.vector(10.5, 10.5)));
    assertTrue(region.test(Tensors.vector(10, 10)));
    assertFalse(region.test(Tensors.vector(8, 8)));
    assertFalse(region.test(Tensors.vector(13, 13)));
  }

  public void testPointsMatrix() {
    Region<Tensor> imageRegion = ImageRegions.loadFromRepository( //
        "/io/track0_100.png", Tensors.vector(10, 10), false);
    Tensor points = RnPointcloudRegions.points((BufferedImageRegion) imageRegion);
    assertEquals(Dimensions.of(points), Arrays.asList(1429, 2));
  }
}
