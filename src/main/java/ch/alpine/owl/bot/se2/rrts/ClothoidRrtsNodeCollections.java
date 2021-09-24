// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.rrts.adapter.NdType;
import ch.alpine.owl.rrts.adapter.NdTypeRrtsNodeCollection;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.opt.nd.NdBox;

public enum ClothoidRrtsNodeCollections {
  ;
  /** @param max
   * @param lbounds
   * @param ubounds
   * @return */
  public static RrtsNodeCollection of(Scalar max, NdBox ndBox) {
    return of(LimitedClothoidNdType.with(max), ndBox);
  }

  private static RrtsNodeCollection of(NdType ndType, NdBox ndBox) {
    return NdTypeRrtsNodeCollection.of(ndType, ndBox);
  }
}
