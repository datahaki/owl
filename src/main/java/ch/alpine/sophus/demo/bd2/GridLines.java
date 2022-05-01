// code by jph
package ch.alpine.sophus.demo.bd2;

import java.util.Objects;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Mod;

/* package */ class GridLines implements TensorScalarFunction {
  private static final Mod MOD = Mod.function(1.0);
  private static final Clip CLIP = Clips.positive(0.25);
  // ---
  private final TensorUnaryOperator tensorUnaryOperator;

  public GridLines(TensorUnaryOperator tensorUnaryOperator) {
    this.tensorUnaryOperator = Objects.requireNonNull(tensorUnaryOperator);
  }

  @Override
  public Scalar apply(Tensor point) {
    for (Tensor _scalar : tensorUnaryOperator.apply(point)) {
      Scalar scalar = (Scalar) _scalar;
      if (FiniteScalarQ.of(scalar)) {
        if (CLIP.isInside(MOD.apply(scalar)))
          return RealScalar.ZERO;
      } else
        return DoubleScalar.INDETERMINATE;
    }
    return RealScalar.ONE;
  }
}