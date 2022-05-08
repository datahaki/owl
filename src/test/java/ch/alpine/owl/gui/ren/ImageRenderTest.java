// code by jph
package ch.alpine.owl.gui.ren;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import ch.alpine.ascona.util.ren.ImageRender;
import ch.alpine.tensor.Tensors;

class ImageRenderTest {
  @Test
  public void testRangeFail1() {
    assertThrows(Exception.class, () -> ImageRender.range(new BufferedImage(50, 20, BufferedImage.TYPE_BYTE_GRAY), Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testRangeFail2() {
    assertThrows(Exception.class, () -> ImageRender.scale(new BufferedImage(50, 20, BufferedImage.TYPE_BYTE_GRAY), Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testImageNullFail() {
    assertThrows(Exception.class, () -> ImageRender.scale(null, Tensors.vector(1, 2)));
  }
}
