// code by jph
package ch.alpine.owl.math.region;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.NdBox;

/** region is open
 * coordinates on the boundary are inside */
public class NdBoxRegion implements Region<Tensor>, Serializable {
  /** @param center
   * @param radius for each coordinate
   * @return */
  public static Region<Tensor> fromCenterAndRadius(Tensor center, Tensor radius) {
    return new NdBoxRegion(NdBox.of( //
        center.subtract(radius), //
        center.add(radius)));
  }

  // ---
  private final NdBox ndBox;

  /** @param ndBox non-null */
  public NdBoxRegion(NdBox ndBox) {
    this.ndBox = Objects.requireNonNull(ndBox);
  }

  @Override
  public boolean isMember(Tensor tensor) {
    return ndBox.isInside(tensor);
  }
}
