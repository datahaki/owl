// code by jph
package ch.alpine.sophus.demo.bd1;

import ch.alpine.sophus.demo.opt.LogWeightings;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.lie.r2.AngleVector;
import ch.alpine.tensor.num.Pi;

// TODO OWL ALG crashes when only 2 control points exist
/* package */ class S1BarycentricCoordinateDemo extends A1BarycentricCoordinateDemo {
  public S1BarycentricCoordinateDemo() {
    super(LogWeightings.list());
  }

  @Override
  Tensor domain(Tensor support) {
    return Subdivide.of(Pi.VALUE.negate(), Pi.VALUE, 128);
  }

  @Override
  Tensor lift(Scalar x) {
    return AngleVector.of(x);
  }

  public static void main(String[] args) {
    new S1BarycentricCoordinateDemo().setVisible(1000, 800);
  }
}
