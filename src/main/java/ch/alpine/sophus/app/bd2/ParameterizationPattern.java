// code by jph
package ch.alpine.sophus.app.bd2;

import java.util.function.Function;

import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** parameterization */
/* package */ enum ParameterizationPattern implements Function<TensorUnaryOperator, TensorScalarFunction> {
  CHECKER_BOARD(CheckerBoard::new), //
  GRID_LINES(GridLines::new), //
  ;

  private final Function<TensorUnaryOperator, TensorScalarFunction> function;

  private ParameterizationPattern(Function<TensorUnaryOperator, TensorScalarFunction> function) {
    this.function = function;
  }

  @Override
  public TensorScalarFunction apply(TensorUnaryOperator tensorUnaryOperator) {
    return function.apply(tensorUnaryOperator);
  }
}
