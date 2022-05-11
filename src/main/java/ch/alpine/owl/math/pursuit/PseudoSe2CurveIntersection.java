// code by jph
package ch.alpine.owl.math.pursuit;

import ch.alpine.sophus.crv.clt.ClothoidDistance;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** DO NOT USE THIS ON A REAL ROBOT */
public class PseudoSe2CurveIntersection extends AssistedCurveIntersection {
  /** @param radius non-negative
   * @throws Exception if given radius is negative */
  public PseudoSe2CurveIntersection(Scalar radius) {
    super(radius, Se2Group.INSTANCE);
  }

  @Override // from SimpleCurveIntersection
  protected Scalar distance(Tensor tensor) {
    return ClothoidDistance.SE2_ANALYTIC.norm(tensor);
  }
}
