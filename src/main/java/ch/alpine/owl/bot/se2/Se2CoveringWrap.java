// code by jph
package ch.alpine.owl.bot.se2;

import ch.alpine.sophis.math.api.CoordinateWrap;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.tensor.Tensor;

/** measures difference between p and q in SE(2) covering group relative to p
 * difference(p, q) = Inv[p] . q */
public enum Se2CoveringWrap implements CoordinateWrap {
  INSTANCE;

  @Override // from CoordinateWrap
  public Tensor represent(Tensor x) {
    return x;
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    return Se2CoveringGroup.INSTANCE.exponential(p).log(q);
  }
}
