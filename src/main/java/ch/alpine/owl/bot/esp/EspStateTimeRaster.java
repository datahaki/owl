// code by jph
package ch.alpine.owl.bot.esp;

import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.math.order.VectorLexicographic;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.alg.Transpose;

/** accounts for symmetries */
enum EspStateTimeRaster implements StateTimeRaster {
  INSTANCE;

  @Override // from StateTimeRaster
  public Tensor convertToKey(StateTime stateTime) {
    Tensor x = stateTime.state();
    Tensor m0 = x.extract(0, 5);
    Tensor m1 = Transpose.of(m0);
    Tensor v0 = Flatten.of(m0);
    Tensor v1 = Flatten.of(m1);
    int compare = VectorLexicographic.COMPARATOR.compare(v0, v1);
    if (0 < compare)
      return x;
    return m1.append(Reverse.of(x.get(5)));
  }
}
