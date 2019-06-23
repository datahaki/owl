// code by jph
package ch.ethz.idsc.sophus.math.win;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public abstract class WindowBaseSampler implements Function<Integer, Tensor>, Serializable {
  protected static final Tensor SINGLETON = Tensors.vector(1).unmodifiable();
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);
  // ---
  protected final ScalarUnaryOperator windowFunction;
  protected final boolean isContinuous;

  /** @param windowFunction for evaluation in the interval [-1/2, +1/2] */
  protected WindowBaseSampler(ScalarUnaryOperator windowFunction) {
    this.windowFunction = windowFunction;
    isContinuous = Chop._03.allZero(windowFunction.apply(RationalScalar.HALF));
  }

  @Override // from IntegerTensorFunction
  public final Tensor apply(Integer extent) {
    return extent == 0 //
        ? SINGLETON
        : NORMALIZE.apply(samples(extent)).unmodifiable();
  }

  /** @param extent
   * @return vector of weights, not normalized */
  protected abstract Tensor samples(int extent);
}
