// code by jph
package ch.alpine.owl.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.util.img.ImageArea;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.io.Import;

class ImageRegionTest {
  @Test
  void testSimple() {
    Tensor image = Import.of("/io/delta_free.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    MemberQ region = new ImageRegion(image, Tensors.vector(179, 128), false);
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
    MemberQ region = new ImageRegion(image, Tensors.vector(179, 128), false);
    for (int x = 0; x < 179; ++x)
      for (int y = 0; y < 128; ++y) {
        assertEquals( //
            Scalars.nonZero(image.Get(y, x)), //
            region.test(Tensors.vector(x, 127 - y, 7)));
      }
  }

  @Test
  void testTensorArea() {
    Tensor image = Tensors.fromString("{{1, 0, 0, 1, 0}}");
    ImageRegion imageRegion = new ImageRegion(image, Tensors.vector(5, 1), true);
    assertTrue(imageRegion.test(Tensors.vector(0.5, 0.5)));
    assertFalse(imageRegion.test(Tensors.vector(1.5, 0.5)));
    assertFalse(imageRegion.test(Tensors.vector(2.5, 0.5)));
    assertTrue(imageRegion.test(Tensors.vector(3.5, 0.5)));
    assertFalse(imageRegion.test(Tensors.vector(4.5, 0.5)));
    assertTrue(imageRegion.test(Tensors.vector(5.5, 0.5)));
    Area area = ImageArea.fromTensor(image);
    assertTrue(area.contains(new Point2D.Double(0.5, 0.5)));
    assertFalse(area.contains(new Point2D.Double(1.5, 0.5)));
    assertFalse(area.contains(new Point2D.Double(2.5, 0.5)));
    assertTrue(area.contains(new Point2D.Double(3.5, 0.5)));
    assertFalse(area.contains(new Point2D.Double(4.5, 0.5)));
    assertFalse(area.contains(new Point2D.Double(5.5, 0.5)));
  }

  @Test
  void testOutsideFalse() {
    Tensor image = Import.of("/io/delta_free.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    MemberQ region = new ImageRegion(image, Tensors.vector(179, 128), false);
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
