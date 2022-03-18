// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import ch.alpine.java.ren.ImageRender;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensors;

public class ImageRenderTest {
  @Test
  public void testRangeFail1() {
    AssertFail.of(() -> ImageRender.range(new BufferedImage(50, 20, BufferedImage.TYPE_BYTE_GRAY), Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testRangeFail2() {
    AssertFail.of(() -> ImageRender.scale(new BufferedImage(50, 20, BufferedImage.TYPE_BYTE_GRAY), Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testImageNullFail() {
    AssertFail.of(() -> ImageRender.scale(null, Tensors.vector(1, 2)));
  }
}
