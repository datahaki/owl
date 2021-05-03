// code by jph
package ch.alpine.sophus.gds;

import java.awt.Dimension;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;

/* package */ class R2ArrayPlot implements GeodesicArrayPlot, Serializable {
  private final Scalar radius;

  public R2ArrayPlot(Scalar radius) {
    this.radius = Objects.requireNonNull(radius);
  }

  @Override // from GeodesicArrayPlot
  public Tensor raster(int resolution, Function<Tensor, ? extends Tensor> function, Tensor fallback) {
    Tensor dx = Subdivide.of(radius.negate(), radius, resolution - 1);
    Tensor dy = Subdivide.of(radius, radius.negate(), resolution - 1);
    return Tensor.of(dy.stream().parallel() //
        .map(py -> Tensor.of(dx.stream() //
            .map(px -> Tensors.of(px, py)) //
            .map(function))));
  }

  @Override // from GeodesicArrayPlot
  public Tensor pixel2model(Dimension dimension) {
    Tensor range = Tensors.of(radius, radius).multiply(RealScalar.TWO);
    Tensor xy = range.multiply(RationalScalar.HALF.negate());
    return GeodesicArrayPlot.pixel2model(xy, range, dimension);
  }
}
