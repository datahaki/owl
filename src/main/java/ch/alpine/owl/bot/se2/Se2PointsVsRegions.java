// code by jph
package ch.alpine.owl.bot.se2;

import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** typical use case of class:
 * extrude region by half-width of vehicle, then give several points along x-axis
 * for obstacle check */
public enum Se2PointsVsRegions {
  ;
  /** the given x_coords are interpreted as the points {{x1, 0}, {x2, 0}, ..., {xN, 0}}
   * 
   * @param x_coords vector {x1, x2, ..., xN}
   * @param region
   * @return instance of Se2PointsVsRegion */
  public static Region<Tensor> line(Tensor x_coords, Region<Tensor> region) {
    Tensor points = x_coords.maps(scalar -> Tensors.of(scalar, scalar.zero()));
    return new Se2PointsVsRegion(points, region);
  }
}
