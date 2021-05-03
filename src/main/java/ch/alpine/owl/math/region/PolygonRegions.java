// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.N;

public enum PolygonRegions {
  ;
  /** @param polygon is mapped to numeric precision */
  public static Region<Tensor> numeric(Tensor polygon) {
    return new PolygonRegion(N.DOUBLE.of(polygon));
  }
}
