// code by jph
package ch.alpine.sophus.ply;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sqrt;

public enum PolygonNormalize {
  ;
  /** @param polygon
   * @param area
   * @return */
  public static Tensor of(Tensor polygon, Scalar area) {
    Scalar factor = Sqrt.FUNCTION.apply(area.divide(PolygonArea.of(polygon)));
    Tensor shift = PolygonCentroid.of(polygon).negate();
    return Tensor.of(polygon.stream().map(shift::add)).multiply(factor);
  }
}
