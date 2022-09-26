// code by jph
package ch.alpine.owl.bot.r2;

import ch.alpine.sophus.math.noise.ContinuousNoise;
import ch.alpine.sophus.math.noise.NativeContinuousNoise;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/* package */ enum ContinuousNoiseUtils {
  ;
  public static ScalarUnaryOperator wrap1D(NativeContinuousNoise nativeContinuousNoise) {
    return wrap1D(nativeContinuousNoise, RealScalar.ZERO);
  }

  public static ScalarUnaryOperator wrap1D(NativeContinuousNoise nativeContinuousNoise, Scalar offset) {
    double value = offset.number().doubleValue();
    return scalar -> RealScalar.of(nativeContinuousNoise.at(scalar.number().doubleValue(), value));
  }

  public static ContinuousNoise wrap2D(NativeContinuousNoise nativeContinuousNoise) {
    return tensor -> RealScalar.of(nativeContinuousNoise.at( //
        tensor.Get(0).number().doubleValue(), //
        tensor.Get(1).number().doubleValue()));
  }

  public static ContinuousNoise wrap3D(NativeContinuousNoise nativeContinuousNoise) {
    return tensor -> RealScalar.of(nativeContinuousNoise.at( //
        tensor.Get(0).number().doubleValue(), //
        tensor.Get(1).number().doubleValue(), //
        tensor.Get(2).number().doubleValue()));
  }
}
