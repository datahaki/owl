// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;

/** @see CoordinateBoundingBox */
@FunctionalInterface
public interface RegionBoundsInterface {
  /** @return box that contains region entirely */
  CoordinateBoundingBox coordinateBounds();
}
