// code by jph
package ch.alpine.owl.bot.esp;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;

/** English sixteen problem
 * 
 * Reference:
 * Sam Loyd/Martin Gardner
 * "Mathematische Raetsel und Spiele" */
/* package */ enum EspModel implements StateSpaceModel {
  INSTANCE;

  @Override
  public Tensor f(Tensor x, Tensor u) {
    Tensor spot = x.get(5);
    int sx = Scalars.intValueExact(spot.Get(0));
    int sy = Scalars.intValueExact(spot.Get(1));
    if (Scalars.nonZero(x.Get(sx, sy)))
      throw TensorRuntimeException.of(x);
    Tensor vacant = spot.add(u);
    int px = Scalars.intValueExact(vacant.Get(0));
    int py = Scalars.intValueExact(vacant.Get(1));
    Tensor y = x.copy();
    y.set(y.Get(px, py), sx, sy);
    y.set(RealScalar.ZERO, px, py);
    y.set(u::add, 5);
    return y;
  }
}
