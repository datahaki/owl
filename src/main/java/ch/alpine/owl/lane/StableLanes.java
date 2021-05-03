// code by gjoel
package ch.alpine.owl.lane;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.red.Nest;

public enum StableLanes {
  ;
  /** @param controlPoints in SE2
   * @param tensorUnaryOperator for instance
   * LaneRiesenfeldCurveSubdivision.of(Clothoids.INSTANCE, 1)::string
   * @param level non-negative
   * @param halfWidth
   * @return */
  public static LaneInterface of( //
      Tensor controlPoints, TensorUnaryOperator tensorUnaryOperator, int level, Scalar halfWidth) {
    return StableLane.of( //
        controlPoints, //
        Nest.of(tensorUnaryOperator, controlPoints, level).unmodifiable(), //
        halfWidth);
  }
}
