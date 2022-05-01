// code by jph
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class ImageEdgesTest {
  @Test
  public void testOne() {
    Tensor image = ImageEdges.extrusion(Tensors.fromString("{{0, 255}, {255, 0}}"), 1);
    assertEquals(image, Tensors.fromString("{{128, 255}, {255, 128}}"));
  }

  @Test
  public void testZero() {
    Tensor image = ImageEdges.extrusion(Tensors.fromString("{{0, 255}, {255, 0}}"), 0);
    assertEquals(image, Tensors.fromString("{{0, 255}, {255, 0}}"));
  }

  @Test
  public void testFail() {
    assertThrows(Exception.class, () -> ImageEdges.extrusion(Tensors.fromString("{{1, 255}, {255, 0}}"), 1));
  }
}
