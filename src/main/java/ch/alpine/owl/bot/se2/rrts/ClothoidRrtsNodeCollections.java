// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.rrts.adapter.NdType;
import ch.alpine.owl.rrts.adapter.NdTypeRrtsNodeCollection;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.VectorQ;

public enum ClothoidRrtsNodeCollections {
  ;
  /** @param max
   * @param lbounds
   * @param ubounds
   * @return */
  public static RrtsNodeCollection of(Scalar max, Tensor lbounds, Tensor ubounds) {
    return of(LimitedClothoidNdType.with(max), lbounds, ubounds);
  }

  private static RrtsNodeCollection of(NdType ndType, Tensor lbounds, Tensor ubounds) {
    return NdTypeRrtsNodeCollection.of(ndType, //
        Append.of(VectorQ.requireLength(lbounds, 2), RealScalar.of(0.0)), //
        Append.of(VectorQ.requireLength(ubounds, 2), RealScalar.of(0.0)));
  }
}
