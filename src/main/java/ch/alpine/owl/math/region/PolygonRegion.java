// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;

import ch.alpine.sophus.crv.d2.Polygons;
import ch.alpine.sophus.math.MinMax;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.sca.N;

/** check if input tensor is inside a polygon in R^2
 * 
 * @see Polygons */
public class PolygonRegion implements Region<Tensor>, Serializable {
  private final NdBox ndBox;
  private final Tensor polygon;

  /** @param polygon as matrix with dimensions n x 2 */
  public PolygonRegion(Tensor polygon) {
    ndBox = MinMax.ndBox(polygon);
    this.polygon = polygon;
  }

  @Override // from Region
  public boolean isMember(Tensor tensor) {
    return ndBox.isInside(tensor) //
        && Polygons.isInside(polygon, tensor);
  }

  public Tensor polygon() {
    return polygon.unmodifiable();
  }

  /** @param polygon is mapped to numeric precision */
  public static Region<Tensor> numeric(Tensor polygon) {
    return new PolygonRegion(N.DOUBLE.of(polygon));
  }
}
