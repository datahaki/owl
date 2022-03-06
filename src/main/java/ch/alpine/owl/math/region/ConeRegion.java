// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;

import ch.alpine.sophus.hs.r2.ArcTan2D;
import ch.alpine.sophus.hs.r2.Se2Bijection;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.lie.r2.AngleVector;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sign;

/** planar infinite cone region in R^2 */
public class ConeRegion implements RegionWithDistance<Tensor>, Serializable {
  private final Tensor apex;
  private final TensorUnaryOperator inverse;
  private final Scalar semi;
  private final Scalar semi_pi_half;
  private final Tensor normal;

  /** @param apex vector of the form {x, y, angle} where {x, y} is the tip of the cone, and
   * angle aligns with the center line of the cone
   * @param semi half angular width of cone,
   * for instance semi==pi/4 corresponds to a cone with a right angle */
  public ConeRegion(Tensor apex, Scalar semi) {
    this.apex = apex;
    inverse = new Se2Bijection(apex).inverse();
    this.semi = Sign.requirePositiveOrZero(semi);
    semi_pi_half = semi.add(Pi.HALF);
    normal = AngleVector.of(semi_pi_half);
  }

  @Override // from Region<Tensor>
  public boolean test(Tensor tensor) {
    Tensor local = inverse.apply(tensor);
    local.set(Abs.FUNCTION, 1); // normalize y coordinate
    Scalar angle = ArcTan2D.of(local); // non-negative
    return Scalars.lessThan(angle, semi);
  }

  @Override // from DistanceFunction<Tensor>
  public Scalar distance(Tensor tensor) {
    Tensor local = inverse.apply(tensor);
    local.set(Abs.FUNCTION, 1); // normalize y coordinate
    Scalar angle = ArcTan2D.of(local); // non-negative
    if (Scalars.lessThan(angle, semi))
      return RealScalar.ZERO;
    return Scalars.lessThan(angle, semi_pi_half) //
        ? (Scalar) normal.dot(local)
        : Vector2Norm.of(local);
  }

  /** @return {x, y, angle} */
  public Tensor apex() {
    return apex;
  }

  /** @return half angular width of cone */
  public Scalar semi() {
    return semi;
  }
}
