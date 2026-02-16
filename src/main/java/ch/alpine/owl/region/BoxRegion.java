// code by jph
package ch.alpine.owl.region;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

/** region is open
 * coordinates on the boundary are inside */
public enum BoxRegion {
  ;
  /** @param center
   * @param radius for each coordinate
   * @return */
  public static MemberQ fromCenterAndRadius(Tensor center, Tensor radius) {
    return CoordinateBounds.of( //
        center.subtract(radius), //
        center.add(radius));
  }
}
