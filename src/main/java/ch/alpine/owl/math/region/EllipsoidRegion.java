// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Sign;

/** EllipsoidRegion implements an axis aligned elliptic region in the vector space R^n.
 * 
 * The region also finds applications for other spaces, such as R^n x R^m
 * where axis depended scaling is desired. One use case is the Lotka-Volterra model.
 * 
 * Notice: evaluate(...) does not correspond to Euclidean distance
 * 
 * @see BallRegion */
public class EllipsoidRegion extends ImplicitFunctionRegion implements RegionBounds, Serializable {
  private final Tensor center;
  private final Tensor radius;
  private final Tensor invert;

  /** The components of radius have to be strictly positive.
   * For a radius equals to zero we would run into numerical trouble:
   * distance Inf when outside, and NaN when at the center
   * 
   * @param center of the ellipsoid
   * @param radius of the different axes with same number of entries as center
   * all components of radius must be strictly positive.
   * if a component of radius is DoubleScalar.POSITIVE_INFINITY, this corresponds to a cylinder
   * @see BallRegion */
  public EllipsoidRegion(Tensor center, Tensor radius) {
    // assert that radius are strictly positive
    if (radius.stream().map(Scalar.class::cast).anyMatch(Sign::isNegativeOrZero))
      throw TensorRuntimeException.of(radius);
    // ---
    this.center = VectorQ.requireLength(center, radius.length()).copy();
    this.radius = radius.copy();
    invert = radius.map(Scalar::reciprocal);
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor tensor) {
    return Vector2NormSquared.of(Times.of(tensor.subtract(center), invert)).subtract(RealScalar.ONE);
  }

  public Tensor center() {
    return center.unmodifiable();
  }

  public Tensor radius() {
    return radius.unmodifiable();
  }

  @Override
  public CoordinateBoundingBox coordinateBounds() {
    return CoordinateBounds.of(center.subtract(radius), center.add(radius));
  }
}
