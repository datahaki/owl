// code by jph
package ch.alpine.owl.math.pursuit;

import java.util.Optional;

import ch.alpine.tensor.Tensor;

public interface CurveIntersection {
  /** @param tensor of points on cyclic trail ahead
   * @return point interpolated on trail at given distance */
  Optional<Tensor> cyclic(Tensor tensor);

  /** @param tensor of points on non-cyclic trail ahead
   * @return point interpolated on trail at given distance */
  Optional<Tensor> string(Tensor tensor);
}
