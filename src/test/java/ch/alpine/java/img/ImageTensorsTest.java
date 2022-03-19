// code by jph
package ch.alpine.java.img;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class ImageTensorsTest {
  @Test
  public void testColor() {
    Tensor tensor = Tensors.fromString("{{{1, 2, 3, 4}, {5, 6, 7, 8}}}");
    Tensor reduce = ImageTensors.reduce(tensor, Tensors.vector(1, 2, 3, 4));
    assertEquals(reduce, Tensors.fromString("{{255, 0}}"));
  }

  @Test
  public void testGrayscale() {
    Tensor tensor = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    Tensor reduce = ImageTensors.reduce(tensor, RealScalar.of(2));
    assertEquals(reduce, Tensors.fromString("{{0, 255, 0, 0}, {0, 0, 0, 0}}"));
  }
}
