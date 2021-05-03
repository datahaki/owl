// code by jph
package ch.alpine.owl.math;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** suitable for time-variant state space models */
public enum VectorFields {
  ;
  public static Tensor of(StateSpaceModel stateSpaceModel, Tensor points, Tensor fallback_u, Scalar factor) {
    TensorUnaryOperator tensorUnaryOperator = //
        x -> Tensors.of(x, x.add(stateSpaceModel.f(x, fallback_u).multiply(factor)));
    return Tensor.of(points.stream().map(tensorUnaryOperator));
  }
}
