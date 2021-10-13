// code by gjoel
package ch.alpine.owl.bot.rn;

import ch.alpine.owl.rrts.adapter.AbstractTransition;
import ch.alpine.owl.rrts.core.TransitionWrap;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Sign;

public class RnTransition extends AbstractTransition {
  public RnTransition(Tensor start, Tensor end) {
    super(start, end, Vector2Norm.between(start, end));
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    return Tensor.of(Subdivide.of(start(), end(), steps(minResolution)).stream().skip(1));
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    int steps = steps(minResolution);
    Scalar resolution = length().divide(RealScalar.of(steps));
    Tensor spacing = ConstantArray.of(resolution, steps);
    return new TransitionWrap(sampled(minResolution), spacing);
  }

  @Override // from Transition
  public Tensor linearized(Scalar minResolution) {
    return Tensors.of(start(), end());
  }

  private int steps(Scalar minResolution) {
    return Ceiling.intValueExact(length().divide(Sign.requirePositive(minResolution)));
  }
}
