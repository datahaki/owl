// code by jph
package ch.alpine.owl.bot.tn;

import ch.alpine.sophis.math.api.CoordinateWrap;
import ch.alpine.tensor.Tensor;

class TnIdentityWrap implements CoordinateWrap {
  @Override // from CoordinateWrap
  public Tensor represent(Tensor x) {
    return x.copy();
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    return q.subtract(p);
  }
}
