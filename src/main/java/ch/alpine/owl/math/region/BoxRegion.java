// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

/** region is open
 * coordinates on the boundary are inside */
public class BoxRegion implements Region<Tensor>, Serializable {
  /** @param center
   * @param radius for each coordinate
   * @return */
  public static Region<Tensor> fromCenterAndRadius(Tensor center, Tensor radius) {
    return new BoxRegion(CoordinateBounds.of( //
        center.subtract(radius), //
        center.add(radius)));
  }

  // ---
  private final CoordinateBoundingBox box;

  /** @param box non-null */
  public BoxRegion(CoordinateBoundingBox box) {
    this.box = Objects.requireNonNull(box);
  }

  @Override
  public boolean test(Tensor tensor) {
    return box.isInside(tensor);
  }
}
