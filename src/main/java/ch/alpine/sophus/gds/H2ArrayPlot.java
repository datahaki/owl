// code by jph
package ch.alpine.sophus.gds;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.alpine.sophus.math.AppendOne;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Subdivide;

/* package */ class H2ArrayPlot implements GeodesicArrayPlot, Serializable {
  private final Scalar radius;

  public H2ArrayPlot(Scalar radius) {
    this.radius = Objects.requireNonNull(radius);
  }

  @Override // from GeodesicArrayPlot
  public Tensor raster(int resolution, Function<Tensor, ? extends Tensor> function, Tensor fallback) {
    Tensor dx = Subdivide.of(radius.negate(), radius, resolution - 1);
    Tensor dy = Subdivide.of(radius, radius.negate(), resolution - 1);
    return Tensor.of(dy.stream().parallel() //
        .map(py -> Tensor.of(dx.stream() //
            .map(px -> Tensors.of(px, py)) //
            .map(HnWeierstrassCoordinate::toPoint) //
            .map(function))));
  }

  @Override // from GeodesicArrayPlot
  public Tensor pixel2model(Dimension dimension) {
    Tensor range = Tensors.of(radius, radius).multiply(RealScalar.TWO); // model
    Tensor scale = Tensors.vector(dimension.width, dimension.height) //
        .pmul(range.map(Scalar::reciprocal)); // model 2 pixel
    return Dot.of( //
        GfxMatrix.translation(range.multiply(RationalScalar.HALF.negate())), //
        AppendOne.FUNCTION.apply(scale.map(Scalar::reciprocal)) // pixel 2 model
            .pmul(GfxMatrix.flipY(dimension.height)));
  }
}
