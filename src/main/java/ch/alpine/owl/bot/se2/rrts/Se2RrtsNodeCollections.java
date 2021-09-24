// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.rrts.adapter.NdTypeRrtsNodeCollection;
import ch.alpine.owl.rrts.adapter.TransitionNdType;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.tensor.opt.nd.NdBox;

/** nearest-neighbor query heuristic backed by NdTreeMap */
public enum Se2RrtsNodeCollections {
  ;
  /** Hint:
   * functionality for {@link ClothoidTransitionSpace} and {@link DubinsTransitionSpace}
   * 
   * @param transitionSpace
   * @param ndBox
   * @return */
  public static RrtsNodeCollection of(TransitionSpace transitionSpace, NdBox ndBox) {
    return NdTypeRrtsNodeCollection.of(new TransitionNdType(transitionSpace), ndBox);
  }
}
