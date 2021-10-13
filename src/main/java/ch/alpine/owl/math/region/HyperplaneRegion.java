// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;

/** region in R^n */
public class HyperplaneRegion extends ImplicitFunctionRegion implements Serializable {
  /** orthogonal is normalized to have Euclidean length 1
   * 
   * @param orthogonal is orthogonal to hyperplane pointing outside
   * @param distanceFromZero needed to reach the region
   * @return */
  public static ImplicitFunctionRegion normalize(Tensor orthogonal, Scalar distanceFromZero) {
    return new HyperplaneRegion(Vector2Norm.NORMALIZE.apply(orthogonal), distanceFromZero);
  }

  // ---
  /** orthogonal to hyperplane pointing outside */
  private final Tensor normal;
  private final Scalar distanceFromZero;

  /** @param normal is normal to hyperplane pointing outside
   * @param distanceFromZero needed to reach the region starting from position (0, ..., 0)
   * That means, if distanceFromZero is negative, (0, ..., 0) is inside the region */
  public HyperplaneRegion(Tensor normal, Scalar distanceFromZero) {
    Tolerance.CHOP.requireClose(Vector2Norm.of(normal), RealScalar.ONE);
    this.normal = normal;
    this.distanceFromZero = distanceFromZero;
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return distanceFromZero.add(x.dot(normal));
  }
}
