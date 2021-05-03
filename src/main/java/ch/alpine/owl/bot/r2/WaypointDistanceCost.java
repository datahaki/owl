// code by ynager
package ch.alpine.owl.bot.r2;

import java.awt.Dimension;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public enum WaypointDistanceCost {
  ;
  /** @param waypoints
   * @param closed if waypoints represent a cycle
   * @param width of path in model space
   * @param model2pixel conversion factor
   * @param dimension of image
   * @return */
  public static ImageCostFunction of( //
      Tensor waypoints, boolean closed, Scalar width, Scalar model2pixel, Dimension dimension) {
    WaypointDistanceImage waypointDistanceImage = new WaypointDistanceImage(waypoints, closed, width, model2pixel, dimension);
    return new SparseImageCostFunction( //
        waypointDistanceImage.image(), //
        waypointDistanceImage.range(), //
        RealScalar.of(WaypointDistanceImage.OFF_PATH_COST));
  }
}
