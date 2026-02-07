// code by jph
package ch.alpine.owl.region;

import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/RegionBounds.html">RegionBounds</a>
 * 
 * @see CoordinateBoundingBox */
@FunctionalInterface
public interface RegionBounds {
  /** @return box that contains region entirely */
  CoordinateBoundingBox coordinateBounds();
}
