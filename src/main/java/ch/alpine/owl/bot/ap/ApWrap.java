// code by astoll
package ch.alpine.owl.bot.ap;

import ch.alpine.owl.math.CoordinateWrap;
import ch.alpine.sophus.lie.so2.So2;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Mod;

/** identifies (x, z, v, gamma) === (x, z, v, gamma + 2 pi n) for all n */
/* package */ enum ApWrap implements CoordinateWrap {
  INSTANCE;

  private static final int INDEX_ANGLE = 3;
  private static final Mod MOD = Mod.function(Pi.TWO);

  @Override // from CoordinateWrap
  public final Tensor represent(Tensor x) {
    Tensor r = x.copy();
    r.set(MOD, INDEX_ANGLE);
    return r;
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    Tensor tensor = q.subtract(p);
    tensor.set(So2.MOD, INDEX_ANGLE);
    return tensor;
  }
}
