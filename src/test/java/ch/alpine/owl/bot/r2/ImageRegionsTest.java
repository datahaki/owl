// code by jph
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.mat.MatrixQ;

public class ImageRegionsTest {
  @Test
  public void testSimple() {
    Tensor tensor = ResourceData.of("/io/track0_100.png");
    assertEquals(Dimensions.of(tensor), Arrays.asList(100, 100, 4));
    Tensor matrix = ImageRegions.grayscale(tensor);
    assertTrue(MatrixQ.of(matrix));
  }

  @Test
  public void testDubendorf() {
    BufferedImage bufferedImage = ResourceData.bufferedImage("/dubilab/localization/20180122.png");
    // Region<Tensor> ir = ImageRegions.loadFromRepository( //
    // "/dubilab/localization/20180122.png", Tensors.vector(10, 10), false);
    // assertEquals(Dimensions.of(ir.image()), Arrays.asList(640, 640));
    assertEquals(bufferedImage.getWidth(), 640);
    assertEquals(bufferedImage.getHeight(), 640);
  }

  @Test
  public void testGrayscale() {
    Tensor image = Tensors.fromString("{{0, 1}, {0, 0}}");
    Tensor output = ImageRegions.grayscale(image);
    assertEquals(image, output);
  }

  @Test
  public void testFail() {
    try {
      ImageRegions.loadFromRepository( //
          "/does/not/exist.png", Tensors.vector(10, 10), false);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
