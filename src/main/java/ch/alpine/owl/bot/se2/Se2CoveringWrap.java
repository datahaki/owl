// code by jph
package ch.alpine.owl.bot.se2;

import ch.alpine.owl.math.CoordinateWrap;
import ch.alpine.sophus.lie.se2c.Se2CoveringGroup;
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
    Tensor tensor = Se2CoveringGroup.INSTANCE.element(p).inverse().combine(q);
    return Se2CoveringGroup.INSTANCE.log(tensor);
  }
}
