// code by gjoel
package ch.alpine.owl.rrts.adapter;

import ch.alpine.sophus.crv.AbstractTransition;
import ch.alpine.sophus.crv.Transition;
import ch.alpine.sophus.crv.TransitionWrap;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public class DirectedTransition extends AbstractTransition {
  protected final Transition transition;
  public final boolean isForward;

  /* package */ DirectedTransition(Transition transition, boolean isForward) {
    super( //
        isForward //
            ? transition.start() //
            : transition.end(), //
        isForward //
            ? transition.end() //
            : transition.start(), //
        transition.length());
    this.transition = transition;
    this.isForward = isForward;
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    return transition.sampled(minResolution);
  }

  @Override // from Transition
  public Tensor linearized(Scalar minResolution) {
    return transition.linearized(minResolution);
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    return transition.wrapped(minResolution);
  }
}
