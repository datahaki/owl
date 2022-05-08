// code by jph
package ch.alpine.owl.bot.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.util.img.ImageArea;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class RegionRendersTest {
  @Test
  public void testSimple() {
    Tensor image = Tensors.fromString("{{1, 0, 1}}");
    BufferedImage bufferedImage = RegionRenders.image(image);
    assertEquals(bufferedImage.getType(), BufferedImage.TYPE_BYTE_GRAY);
    int[] pixel = new int[1];
    bufferedImage.getRaster().getPixel(0, 0, pixel);
    assertEquals(pixel[0], RegionRenders.RGB);
    Color color = new Color(bufferedImage.getRGB(0, 0));
    assertEquals(color.getRed(), 244); // 230 get's mapped to 244
    Area area = ImageArea.fromTensor(image);
    assertTrue(area.contains(new Point2D.Double(0.5, 0.5)));
    assertFalse(area.contains(new Point2D.Double(1.5, 0.5)));
    assertTrue(area.contains(new Point2D.Double(2.5, 0.5)));
  }
}
