// code by jph
package ch.alpine.owl.bot.kl;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;

/** u == {index, dx, dy} */
/* package */ enum KlotskiModel implements StateSpaceModel {
  INSTANCE;

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Tensor y = x.copy();
    int index = Scalars.intValueExact(u.Get(0));
    Scalar dx = u.Get(1);
    Scalar dy = u.Get(2);
    y.set(dx::add, index, 1);
    y.set(dy::add, index, 2);
    return y;
  }
}
