// code by jph
package ch.alpine.owl.bot.psu;

import ch.alpine.owl.math.CoordinateWrap;
import ch.alpine.sophus.lie.so2.So2;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Mod;

/** singleton instance */
/* package */ enum PsuWrap implements CoordinateWrap {
  INSTANCE;

  private static final Mod MOD = Mod.function(Pi.TWO);

  @Override // from CoordinateWrap
  public Tensor represent(Tensor x) {
    return Tensors.of(MOD.apply(x.Get(0)), x.Get(1));
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    Tensor d = p.subtract(q);
    d.set(So2.MOD, 0);
    return d;
  }
}
