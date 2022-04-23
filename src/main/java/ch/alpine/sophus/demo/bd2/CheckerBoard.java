// code by jph
package ch.alpine.sophus.demo.bd2;

import java.util.Objects;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.FiniteQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Floor;

/* package */ class CheckerBoard implements TensorScalarFunction {
  private final TensorUnaryOperator tensorUnaryOperator;

  public CheckerBoard(TensorUnaryOperator tensorUnaryOperator) {
    this.tensorUnaryOperator = Objects.requireNonNull(tensorUnaryOperator);
  }

  @Override
  public Scalar apply(Tensor point) {
    Scalar scalar = Total.ofVector(tensorUnaryOperator.apply(point).map(Floor.FUNCTION));
    if (FiniteQ.of(scalar))
      return RealScalar.of(Math.floorMod(scalar.number().intValue(), 2));
    return DoubleScalar.INDETERMINATE;
  }
}