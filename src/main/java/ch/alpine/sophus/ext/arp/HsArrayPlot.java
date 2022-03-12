// code by jph
package ch.alpine.sophus.ext.arp;

import java.awt.Dimension;
import java.util.function.Function;

import ch.alpine.java.fig.ArrayPlot;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.math.AppendOne;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.red.Times;

/** @see ArrayPlot */
public interface HsArrayPlot {
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
    Tensor scale = Times.of(range, Tensors.vector(dimension.width, dimension.height).map(Scalar::reciprocal));
    return Dot.of( //
        GfxMatrix.translation(xy), //
        Times.of(AppendOne.FUNCTION.apply(scale), GfxMatrix.flipY(dimension.height)));
  }
}
