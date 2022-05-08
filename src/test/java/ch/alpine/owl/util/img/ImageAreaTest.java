// code by jph
package ch.alpine.owl.util.img;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class ImageAreaTest {
  @Test
  public void testBlackWhite() throws IOException {
    BufferedImage bufferedImage = image("/dubilab/obstacles/20180423.png");
    Area area = ImageArea.fromImage(bufferedImage); // takes ~6[s]
    Rectangle rectangle = area.getBounds();
    assertEquals(rectangle.x, 16);
    assertEquals(rectangle.y, 16);
    assertEquals(rectangle.width, 593);
    assertEquals(rectangle.height, 585);
  }

  private static BufferedImage image(String string) throws IOException {
    try (InputStream inputStream = ImageAreaTest.class.getResourceAsStream(string)) { // auto closeable
      return ImageIO.read(inputStream);
    }
  }

  @Test
  public void testTensorArea() {
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
  public void testFail() {
    assertThrows(Exception.class, () -> ImageArea.fromImage(null));
  }
}
