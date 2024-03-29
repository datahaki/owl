// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Mod;
import ch.alpine.tensor.sca.Sign;

/** region describes a section of the unit circle */
public final class So2Region extends ImplicitRegionWithDistance implements Serializable {
  /** @param center angular destination
   * @param radius tolerance non-negative */
  public static So2Region periodic(Scalar center, Scalar radius) {
    return new So2Region(center, radius, Pi.VALUE);
  }

  /** @param center angular destination
   * @param radius tolerance non-negative */
  public static So2Region covering(Scalar center, Scalar radius) {
    return new So2Region(center, radius);
  }

  // ---
  private final Scalar center;
  private final Scalar radius;
  private final ScalarUnaryOperator mod;

  // constructor exists to test with units
  /* package */ So2Region(Scalar center, Scalar radius, Scalar half_circumference) {
    this.center = Objects.requireNonNull(center);
    this.radius = Sign.requirePositiveOrZero(radius);
    mod = Mod.function( //
        half_circumference.add(half_circumference), // 2*half_circumference
        half_circumference.negate());
  }

  // constructor exists to test with units
  private So2Region(Scalar center, Scalar radius) {
    this.center = Objects.requireNonNull(center);
    this.radius = Sign.requirePositiveOrZero(radius);
    mod = scalar -> scalar;
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return Abs.FUNCTION.apply(mod.apply(center.subtract(x))).subtract(radius);
  }

  /** @return center angle of region on unit circle */
  public Scalar center() {
    return center;
  }

  /** @return angular radius of region on unit circle */
  public Scalar radius() {
    return radius;
  }
}
