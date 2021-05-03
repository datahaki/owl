// code by jph
package ch.alpine.owl.bot.r2;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class ImageEdgesTest extends TestCase {
  public void testOne() {
    Tensor image = ImageEdges.extrusion(Tensors.fromString("{{0, 255}, {255, 0}}"), 1);
    assertEquals(image, Tensors.fromString("{{128, 255}, {255, 128}}"));
  }

  public void testZero() {
    Tensor image = ImageEdges.extrusion(Tensors.fromString("{{0, 255}, {255, 0}}"), 0);
    assertEquals(image, Tensors.fromString("{{0, 255}, {255, 0}}"));
  }

  public void testFail() {
    AssertFail.of(() -> ImageEdges.extrusion(Tensors.fromString("{{1, 255}, {255, 0}}"), 1));
  }
}
