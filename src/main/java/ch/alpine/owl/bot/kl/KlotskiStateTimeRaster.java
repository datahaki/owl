// code by jph
package ch.alpine.owl.bot.kl;

import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.math.order.VectorLexicographic;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Tensor;

/* package */ enum KlotskiStateTimeRaster implements StateTimeRaster {
  INSTANCE;

  @Override // from StateTimeRaster
  public Tensor convertToKey(StateTime stateTime) {
    return convertToKey(stateTime.state());
  }

  public static Tensor convertToKey(Tensor state) {
    return Tensor.of(state.stream().sorted(VectorLexicographic.COMPARATOR));
  }
}
