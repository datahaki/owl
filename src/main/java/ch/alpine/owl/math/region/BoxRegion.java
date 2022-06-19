// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

/** region is open
 * coordinates on the boundary are inside */
public class BoxRegion implements Region<Tensor>, RegionBounds, Serializable {
  /** @param center
   * @param radius for each coordinate
   * @return */
  public static Region<Tensor> fromCenterAndRadius(Tensor center, Tensor radius) {
    return new BoxRegion(CoordinateBounds.of( //
        center.subtract(radius), //
        center.add(radius)));
  }

  // ---
  private final CoordinateBoundingBox coordinateBoundingBox;

  /** @param coordinateBoundingBox non-null */
  public BoxRegion(CoordinateBoundingBox coordinateBoundingBox) {
    this.coordinateBoundingBox = Objects.requireNonNull(coordinateBoundingBox);
  }

  @Override
  public boolean test(Tensor tensor) {
    return coordinateBoundingBox.isInside(tensor);
  }

  @Override
  public CoordinateBoundingBox coordinateBounds() {
    return coordinateBoundingBox;
  }
}
