// code by jph
package ch.alpine.owl.data.img;

import java.awt.image.BufferedImage;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ImageFormat;
import junit.framework.TestCase;

public class ImageAlphaTest extends TestCase {
  public void testRgba() {
    Tensor tensor = Tensors.fromString("{{{255, 255, 255, 255}, {128, 0, 255, 128}}}");
    BufferedImage bufferedImage = ImageFormat.of(tensor);
    BufferedImage image = ImageAlpha.scale(bufferedImage, 0.5f);
    Tensor from = ImageFormat.from(image);
    assertEquals(from, Tensors.fromString("{{{255, 255, 255, 127}, {128, 0, 255, 64}}}"));
  }

  public void testFailGray() {
    Tensor tensor = Tensors.fromString("{{255, 255, 255, 255}, {128, 0, 255, 128}}");
    BufferedImage bufferedImage = ImageFormat.of(tensor);
    AssertFail.of(() -> ImageAlpha.scale(bufferedImage, 0.5f));
  }

  public void testGray() {
    Tensor tensor = Tensors.fromString("{{255, 255, 255, 255}, {128, 0, 255, 128}}");
    BufferedImage bufferedImage = ImageFormat.of(tensor);
    BufferedImage image = ImageAlpha.grayscale(bufferedImage, 0.5f);
    Tensor from = ImageFormat.from(image);
    assertEquals(from, Tensors.fromString("{{255, 255, 255, 255}, {192, 128, 255, 192}}"));
  }
}
