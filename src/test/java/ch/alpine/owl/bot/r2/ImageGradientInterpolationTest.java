// code by jph
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.io.ResourceData;

class ImageGradientInterpolationTest {
  @Test
  public void testLinear() {
    Tensor range = Tensors.vector(9, 6.5);
    Tensor res;
    Scalar max;
    final Tensor image = ResourceData.of("/io/delta_uxy.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    {
      ImageGradientInterpolation imageGradientInterpolation = //
          ImageGradientInterpolation.linear(image, range, RealScalar.of(0.5));
      res = imageGradientInterpolation.get(Tensors.vector(2, 3));
      max = imageGradientInterpolation.maxNormGradient();
    }
    {
      ImageGradientInterpolation imageGradientInterpolation = //
          ImageGradientInterpolation.linear(image, range, RealScalar.ONE);
      Tensor cmp = imageGradientInterpolation.get(Tensors.vector(2, 3));
      assertEquals(cmp, res.multiply(RealScalar.of(2)));
      assertEquals(imageGradientInterpolation.maxNormGradient(), max.multiply(RealScalar.of(2)));
    }
  }

  @Test
  public void testNearest() {
    Tensor range = Tensors.vector(9, 6.5);
    Tensor res;
    Scalar max;
    final Tensor image = ResourceData.of("/io/delta_uxy.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    {
      ImageGradientInterpolation imageGradientInterpolation = //
          ImageGradientInterpolation.nearest(image, range, RealScalar.of(0.5));
      res = imageGradientInterpolation.get(Tensors.vector(2, 3));
      max = imageGradientInterpolation.maxNormGradient();
    }
    {
      ImageGradientInterpolation imageGradientInterpolation = //
          ImageGradientInterpolation.nearest(image, range, RealScalar.ONE);
      Tensor cmp = imageGradientInterpolation.get(Tensors.vector(2, 3));
      assertEquals(cmp, res.multiply(RealScalar.of(2)));
      assertEquals(imageGradientInterpolation.maxNormGradient(), max.multiply(RealScalar.of(2)));
      assertEquals(imageGradientInterpolation.get(Tensors.vector(22, -3)), Array.zeros(2));
    }
  }

  @Test
  public void testSerialize() throws Exception {
    Tensor range = Tensors.vector(9, 6.5);
    final Tensor image = ResourceData.of("/io/delta_uxy.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    ImageGradientInterpolation imageGradientInterpolation = //
        ImageGradientInterpolation.linear(image, range, RealScalar.of(0.5));
    Serialization.copy(imageGradientInterpolation);
  }
}
