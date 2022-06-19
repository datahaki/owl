// code by jph
package ch.alpine.owl.util.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ImageFormat;

class ImageAlphaTest {
  @Test
  void testRgba() {
    Tensor tensor = Tensors.fromString("{{{255, 255, 255, 255}, {128, 0, 255, 128}}}");
    BufferedImage bufferedImage = ImageFormat.of(tensor);
    BufferedImage image = ImageAlpha.scale(bufferedImage, 0.5f);
    Tensor from = ImageFormat.from(image);
    assertEquals(from, Tensors.fromString("{{{255, 255, 255, 127}, {128, 0, 255, 64}}}"));
  }

  @Test
  void testFailGray() {
    Tensor tensor = Tensors.fromString("{{255, 255, 255, 255}, {128, 0, 255, 128}}");
    BufferedImage bufferedImage = ImageFormat.of(tensor);
    assertThrows(Exception.class, () -> ImageAlpha.scale(bufferedImage, 0.5f));
  }

  @Test
  void testGray() {
    Tensor tensor = Tensors.fromString("{{255, 255, 255, 255}, {128, 0, 255, 128}}");
    BufferedImage bufferedImage = ImageFormat.of(tensor);
    BufferedImage image = ImageAlpha.grayscale(bufferedImage, 0.5f);
    Tensor from = ImageFormat.from(image);
    assertEquals(from, Tensors.fromString("{{255, 255, 255, 255}, {192, 128, 255, 192}}"));
  }
}
