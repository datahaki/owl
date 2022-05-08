// code by gjoel
package ch.alpine.owl.rrts.adapter;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.alpine.sophus.api.Transition;
import ch.alpine.sophus.api.TransitionSpace;
import ch.alpine.sophus.api.TransitionWrap;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Sign;

public class ReversalTransitionSpace implements TransitionSpace, Serializable {
  /** @param transitionSpace
   * @return */
  public static TransitionSpace of(TransitionSpace transitionSpace) {
    return new ReversalTransitionSpace(transitionSpace);
  }

  // ---
  private final TransitionSpace transitionSpace;

  private ReversalTransitionSpace(TransitionSpace transitionSpace) {
    this.transitionSpace = transitionSpace;
  }

  @Override // from TransitionSpace
  public DirectedTransition connect(Tensor start, Tensor end) {
    Transition transition = transitionSpace.connect(end, start);
    return new ReversalTransition(transition) {
      @Override // from Transition
      public TransitionWrap wrapped(Scalar minResolution) {
        Sign.requirePositive(minResolution);
        int steps = Integers.requirePositive(Ceiling.intValueExact(length().divide(minResolution)));
        Tensor samples = sampled(length().divide(RealScalar.of(steps)));
        Tensor spacing = Array.zeros(samples.length());
        // TODO OWL ALG use of function "connect" does not give subsegments of transition generally
        IntStream.range(0, samples.length()) //
            .forEach(i -> spacing.set(i > 0 //
                ? connect(samples.get(i - 1), samples.get(i)).length() //
                : connect(start, samples.get(0)).length(), i));
        return new TransitionWrap(samples, spacing);
      }
    };
  }
}
