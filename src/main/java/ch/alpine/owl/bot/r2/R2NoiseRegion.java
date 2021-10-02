// code by jph
package ch.alpine.owl.bot.r2;

import java.io.Serializable;

import ch.alpine.owl.math.noise.ContinuousNoise;
import ch.alpine.owl.math.noise.ContinuousNoiseUtils;
import ch.alpine.owl.math.noise.SimplexContinuousNoise;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;

/** {@link R2NoiseRegion} is an implicit function region.
 * 
 * the simplex noise function is a continuous bivariate function with values in the interval [-1, 1]
 * https://de.wikipedia.org/wiki/Simplex_Noise
 * 
 * membership in the region for coordinates (x, y) that evaluate the noise function above a given threshold. */
public class R2NoiseRegion implements Region<Tensor>, Serializable {
  private static final ContinuousNoise CONTINUOUS_NOISE = //
      ContinuousNoiseUtils.wrap2D(SimplexContinuousNoise.FUNCTION);
  // ---
  private final Scalar threshold;

  /** @param threshold in the interval [-1, 1] */
  public R2NoiseRegion(Scalar threshold) {
    this.threshold = threshold;
  }

  @Override // from Region
  public boolean test(Tensor tensor) {
    return Scalars.lessThan(threshold, CONTINUOUS_NOISE.apply(Extract2D.FUNCTION.apply(tensor)));
  }
}
