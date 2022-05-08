// code by jph
package ch.alpine.owl.rrts.adapter;

import ch.alpine.sophus.api.Transition;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;

public abstract class ReversalTransition extends DirectedTransition {
  /* package */ ReversalTransition(Transition transition) {
    super(transition, false);
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    return swap(transition.sampled(minResolution));
  }

  private Tensor swap(Tensor samples) {
    // return Reverse.of(samples.extract(1, samples.length()).append(start()));
    return Reverse.of(samples.extract(0, samples.length() - 1)).append(end());
  }
}
