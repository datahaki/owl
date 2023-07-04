// code by jph
package ch.alpine.owl.bot.se2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.region.Regions;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.red.MinMax;
import ch.alpine.tensor.sca.Clip;

class Se2PointsVsRegionsTest {
  @Test
  void testSimple() {
    Region<Tensor> region = Se2PointsVsRegions.line(Tensors.vector(-2, 1, 0, 5), Regions.emptyRegion());
    assertFalse(region.test(Tensors.vector(1, 2, 3, 4))); // interpretation as xya
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> Se2PointsVsRegions.line(Tensors.vector(-2, 1, 0, 5), null));
  }

  @Test
  void testFootprint() {
    Tensor shape = Import.of("/gokart/footprint/20171201.csv");
    Clip clip = shape.stream().map(tensor -> tensor.Get(0)).collect(MinMax.toClip());
    Tensor x_coords = Subdivide.increasing(clip, 3);
    Tensor center = Tensors.vector(2, 0);
    Region<Tensor> region = new EllipsoidRegion(center, Tensors.vector(1, 1));
    Region<Tensor> query = Se2PointsVsRegions.line(x_coords, region);
    assertTrue(query.test(Tensors.vector(0, 0, 0)));
    assertFalse(query.test(Tensors.vector(0, 0, 3.14 / 2)));
    assertFalse(query.test(Tensors.vector(0, 0, 3.14)));
    assertFalse(query.test(Tensors.vector(0, 0, 3 * 3.14 / 2)));
  }
}
