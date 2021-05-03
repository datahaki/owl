// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.rrts.adapter.NdTypeRrtsNodeCollection;
import ch.alpine.owl.rrts.adapter.TransitionNdType;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.VectorQ;

/** nearest-neighbor query heuristic backed by NdTreeMap */
public enum Se2RrtsNodeCollections {
  ;
  private static final Scalar ZERO = RealScalar.of(0.0);

  /** Hint:
   * functionality for {@link Legendre3ClothoidTransitionSpace} and {@link DubinsTransitionSpace}
   * 
   * @param transitionSpace
   * @param lbounds vector of length 2
   * @param ubounds vector of length 2
   * @return */
  public static RrtsNodeCollection of(TransitionSpace transitionSpace, Tensor lbounds, Tensor ubounds) {
    return NdTypeRrtsNodeCollection.of( //
        new TransitionNdType(transitionSpace), //
        Append.of(VectorQ.requireLength(lbounds, 2), ZERO), //
        Append.of(VectorQ.requireLength(ubounds, 2), ZERO));
  }
}
