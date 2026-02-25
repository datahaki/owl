// code by jph
package ch.alpine.owl.klotzki;

import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.math.order.VectorLexicographic;
import ch.alpine.owlets.math.state.StateTime;
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
