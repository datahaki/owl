// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.math.AppendOne;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sqrt;

/* package */ enum S2ArrayHelper {
  ;
  public static Scalar[][] of(int resolution, double rad, TensorScalarFunction tensorScalarFunction) {
    Tensor dx = Subdivide.of(-rad, +rad, resolution);
    Tensor dy = Subdivide.of(+rad, -rad, resolution);
    int rows = dy.length();
    int cols = dx.length();
    Scalar[][] array = new Scalar[rows][cols];
    Clip clip = Clips.unit();
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = Tensors.of(dx.get(cx), dy.get(cy)); // in R2
        Scalar z2 = RealScalar.ONE.subtract(Vector2NormSquared.of(point));
        if (Sign.isPositive(z2)) {
          Scalar z = Sqrt.FUNCTION.apply(z2);
          Tensor xyz = point.append(z);
          array[cy][cx] = clip.apply(tensorScalarFunction.apply(xyz));
        } else
          array[cy][cx] = DoubleScalar.INDETERMINATE;
      }
    });
    return array;
  }

  static Tensor pixel2model(BufferedImage bufferedImage, double rad) {
    Tensor range = Tensors.vector(rad, rad).multiply(RealScalar.TWO); // model
    Tensor scale = Times.of(Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()), //
        range.map(Scalar::reciprocal)); // model 2 pixel
    return Dot.of( //
        GfxMatrix.translation(range.multiply(RationalScalar.HALF.negate())), //
        Times.of(AppendOne.FUNCTION.apply(scale.map(Scalar::reciprocal)), // pixel 2 model
            GfxMatrix.flipY(bufferedImage.getHeight())));
  }
}
