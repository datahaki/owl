// code by jph
package ch.alpine.owl.math.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.io.Import;

class ImageRegionTest {
  @Test
  void testSimple() {
    Tensor image = Import.of("/io/delta_free.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    Region<Tensor> region = new ImageRegion(image, Tensors.vector(179, 128), false);
    for (int x = 0; x < 179; ++x)
      for (int y = 0; y < 128; ++y) {
        assertEquals( //
            Scalars.nonZero(image.Get(y, x)), //
            region.test(Tensors.vector(x, 127 - y)));
      }
  }

  @Test
  void testCorner() {
    Tensor image = Import.of("/io/delta_free.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    Region<Tensor> region = new ImageRegion(image, Tensors.vector(179, 128), false);
    for (int x = 0; x < 179; ++x)
      for (int y = 0; y < 128; ++y) {
        assertEquals( //
            Scalars.nonZero(image.Get(y, x)), //
            region.test(Tensors.vector(x, 127 - y, 7)));
      }
  }

  @Test
  void testOutsideFalse() {
    Tensor image = Import.of("/io/delta_free.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    Region<Tensor> region = new ImageRegion(image, Tensors.vector(179, 128), false);
    assertFalse(region.test(Tensors.vector(-100, -2000, 3)));
  }

  @Test
  void testOutsideTrue() {
    Tensor image = Import.of("/io/delta_free.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    Tensor range = Tensors.vector(179, 128);
    ImageRegion region = new ImageRegion(image, range, true);
    assertEquals(region.range(), range);
    assertTrue(region.test(Tensors.vector(-100, -2000, 3)));
    assertEquals(region.origin(), Array.zeros(2));
  }
}
