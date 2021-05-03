// code by gjoel
package ch.alpine.owl.math.pursuit;

import ch.alpine.sophus.lie.se2.Se2Geodesic;
import ch.alpine.sophus.math.d2.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2Norm;

/** intersection of SE2 curve with 2-dimensional sphere centered at (0, 0) and given radius. */
public class SphereSe2CurveIntersection extends AssistedCurveIntersection {
  /** @param radius non-negative
   * @throws Exception if given radius is negative */
  public SphereSe2CurveIntersection(Scalar radius) {
    super(radius, Se2Geodesic.INSTANCE);
  }

  @Override // from SimpleCurveIntersection
  protected Scalar distance(Tensor tensor) {
    return Vector2Norm.of(Extract2D.FUNCTION.apply(tensor));
  }
}
