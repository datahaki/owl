// code by jph
package ch.alpine.owl.bot.r2;

import java.io.Serializable;

import ch.alpine.owl.math.noise.ContinuousNoise;
import ch.alpine.owl.math.noise.ContinuousNoiseUtils;
import ch.alpine.owl.math.noise.SimplexContinuousNoise;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;

/** the simplex noise function is a continuous bivariate function with values in the interval [-1, 1]
 * https://de.wikipedia.org/wiki/Simplex_Noise
 * 
 * membership in the region for coordinates (x, y, t) that evaluate the noise function above a given threshold */
public class R2xTNoiseStateTimeRegion implements Region<StateTime>, Serializable {
  private static final ContinuousNoise CONTINUOUS_NOISE = //
      ContinuousNoiseUtils.wrap3D(SimplexContinuousNoise.FUNCTION);
  // ---
  private final Scalar threshold;

  /** @param threshold in the interval [-1, 1] */
  public R2xTNoiseStateTimeRegion(Scalar threshold) {
    this.threshold = threshold;
  }

  @Override // from Region
  public boolean isMember(StateTime stateTime) {
    Tensor tensor = Extract2D.FUNCTION.apply(stateTime.state()).append(stateTime.time());
    return Scalars.lessThan(threshold, CONTINUOUS_NOISE.apply(tensor));
  }
}
