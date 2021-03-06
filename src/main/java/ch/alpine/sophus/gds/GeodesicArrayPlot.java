// code by jph
package ch.alpine.sophus.gds;

import java.awt.Dimension;
import java.util.function.Function;

import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.sophus.math.AppendOne;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.img.ArrayPlot;

/** @see ArrayPlot */
public interface GeodesicArrayPlot {
  /** @param resolution
   * @param function
   * @param fallback
   * @return */
  Tensor raster(int resolution, Function<Tensor, ? extends Tensor> function, Tensor fallback);

  /** @param dimension
   * @return */
  Tensor pixel2model(Dimension dimension);

  /** @param xy lower left corner
   * @param range of image in model space
   * @param dimension of image
   * @return */
  static Tensor pixel2model(Tensor xy, Tensor range, Dimension dimension) {
    // pixel 2 model
    Tensor scale = range.pmul(Tensors.vector(dimension.width, dimension.height).map(Scalar::reciprocal));
    return Dot.of( //
        Se2Matrix.translation(xy), //
        AppendOne.FUNCTION.apply(scale).pmul(Se2Matrix.flipY(dimension.height)));
  }
}
