// code by jph, gjoel
package ch.alpine.owl.bot.rn;

import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.tensor.Tensor;

public enum RnTransitionSpace implements TransitionSpace {
  INSTANCE;

  @Override // from TransitionSpace
  public RnTransition connect(Tensor start, Tensor end) {
    return new RnTransition(start, end);
  }
}
