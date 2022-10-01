// code by ynager
package ch.alpine.owl.math.region;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

/** interval region in R^1 */
public class LinearRegion extends ImplicitRegionWithDistance implements Serializable {
  private final Scalar center;
  private final Scalar radius;

  /** @param center angular destination, non-null
   * @param radius non-negative tolerance
   * @throws Exception if either input parameter violates specifications */
  public LinearRegion(Scalar center, Scalar radius) {
    this.center = Objects.requireNonNull(center);
    this.radius = Sign.requirePositiveOrZero(radius);
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return Abs.between(center, (Scalar) x).subtract(radius);
  }

  /** @return center of region */
  public Scalar center() {
    return center;
  }

  /** @return radius of region */
  public Scalar radius() {
    return radius;
  }

  /** @return region as clip interval */
  public Clip clip() {
    return Clips.centered(center, radius);
  }
}
