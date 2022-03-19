// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;

import ch.alpine.sophus.api.RegionBoundsInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.sca.Sign;

/** the ball region is a special case of an {@link EllipsoidRegion}.
 * 
 * <p>{@link BallRegion} is implemented separately, because the implementation
 * 1) requires less operations than if treated as an elliptic case
 * 2) is numerically more stable in corner cases
 * 
 * <p>the function {@link #uniform(Tensor)} returns the minimal Euclidean distance
 * that is separating the input coordinate from the spherical region, and negative
 * values when inside the spherical region.
 * 
 * <p>for radius == 0, the region evaluates
 * <ul>
 * <li>zero in a single point: the center, and
 * <li>negative nowhere
 * </ul> */
public class BallRegion extends ImplicitRegionWithDistance implements RegionBoundsInterface, Serializable {
  private final Tensor center;
  private final Scalar radius;

  /** @param center vector with length() == n
   * @param radius non-negative */
  public BallRegion(Tensor center, Scalar radius) {
    this.center = VectorQ.require(center).copy();
    this.radius = Sign.requirePositiveOrZero(radius);
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    // ||x - center|| - radius
    return Vector2Norm.between(x, center).subtract(radius); // result may be negative
  }

  public Tensor center() {
    return center.unmodifiable();
  }

  public Scalar radius() {
    return radius;
  }

  @Override // from RegionBoundsInterface
  public CoordinateBoundingBox coordinateBounds() {
    Scalar n_radius = radius.negate();
    return CoordinateBounds.of( //
        center.map(n_radius::add), //
        center.map(radius::add));
  }
}
