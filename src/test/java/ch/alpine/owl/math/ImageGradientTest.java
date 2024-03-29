// code by jph
package ch.alpine.owl.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;

class ImageGradientTest {
  @Test
  void testSimple() {
    Tensor image = Tensors.fromString("{{1, 5}, {2, 3}, {4, 5}}");
    assertEquals(Dimensions.of(image), Arrays.asList(3, 2));
    Tensor grad = ImageGradient.of(image);
    assertEquals(grad, Tensors.fromString("{{{1, 4}}, {{2, 1}}}"));
    assertEquals(Dimensions.of(grad), Arrays.asList(2, 1, 2));
    Tensor diffx = grad.get(Tensor.ALL, Tensor.ALL, 0);
    assertEquals(diffx, Tensors.fromString("{{1}, {2}}"));
    Tensor diffy = grad.get(Tensor.ALL, Tensor.ALL, 1);
    assertEquals(diffy, Tensors.fromString("{{4}, {1}}"));
  }

  @Test
  void testRotated() {
    Tensor image = Tensors.fromString("{{1, 5}, {2, 3}, {4, 5}}");
    assertEquals(Dimensions.of(image), Arrays.asList(3, 2));
    Tensor grad = ImageGradient.rotated(image);
    assertEquals(grad, Tensors.fromString("{{{-4, 1}}, {{-1, 2}}}"));
    assertEquals(Dimensions.of(grad), Arrays.asList(2, 1, 2));
  }
}
