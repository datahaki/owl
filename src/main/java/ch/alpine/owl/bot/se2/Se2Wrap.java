// code by jph
package ch.alpine.owl.bot.se2;

import ch.alpine.owl.math.CoordinateWrap;
import ch.alpine.sophus.lie.se2.Se2GroupElement;
import ch.alpine.sophus.lie.se2c.Se2CoveringExponential;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Mod;

/** identifies (x, y, theta) === (x, y, theta + 2 pi n) for all n
 * 
 * representation of angles is in the interval [0, 2pi)
 * 
 * differences are mapped to [-pi, pi)
 * 
 * @see Se2CoveringWrap */
public enum Se2Wrap implements CoordinateWrap {
  INSTANCE;

  private static final int INDEX_ANGLE = 2;
  private static final Mod MOD = Mod.function(Pi.TWO);

  @Override // from CoordinateWrap
  public final Tensor represent(Tensor x) {
    Tensor r = x.copy();
    r.set(MOD, INDEX_ANGLE);
    return r;
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    return Se2CoveringExponential.INSTANCE.log(new Se2GroupElement(p).inverse().combine(q));
  }
}
