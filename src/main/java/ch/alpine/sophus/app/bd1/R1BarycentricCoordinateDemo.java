// code by jph
package ch.alpine.sophus.app.bd1;

import ch.alpine.sophus.opt.MixedLogWeightings;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.N;

/* package */ class R1BarycentricCoordinateDemo extends A1BarycentricCoordinateDemo {
  private static final Scalar MARGIN = RealScalar.of(2);

  public R1BarycentricCoordinateDemo() {
    super(MixedLogWeightings.scattered());
  }

  @Override
  Tensor domain(Tensor support) {
    return Subdivide.of( //
        support.stream().reduce(Min::of).get().add(MARGIN.negate()), //
        support.stream().reduce(Max::of).get().add(MARGIN), 128).map(N.DOUBLE);
  }

  @Override
  Tensor lift(Scalar x) {
    return Tensors.of(x);
  }

  public static void main(String[] args) {
    new R1BarycentricCoordinateDemo().setVisible(1000, 800);
  }
}
