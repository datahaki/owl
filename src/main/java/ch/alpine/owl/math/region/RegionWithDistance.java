// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.owl.math.DistanceFunction;
import ch.alpine.sophus.api.Region;

/** Interface for region with distance function that indicates proximity of a given point to the region.
 * The distance function returns 0 for members of the region, and positive values for non-members. */
public interface RegionWithDistance<T> extends Region<T>, DistanceFunction<T> {
  // ---
}
