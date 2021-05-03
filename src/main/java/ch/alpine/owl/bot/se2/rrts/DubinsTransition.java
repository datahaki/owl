// code by gjoel, jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.rrts.adapter.AbstractTransition;
import ch.alpine.owl.rrts.core.TransitionWrap;
import ch.alpine.sophus.crv.dubins.DubinsPath;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Sign;

/* package */ class DubinsTransition extends AbstractTransition {
  // ---
  private final DubinsPath dubinsPath;

  public DubinsTransition(Tensor start, Tensor end, DubinsPath dubinsPath) {
    super(start, end, dubinsPath.length());
    this.dubinsPath = dubinsPath;
  }

  /** @param minResolution strictly positive
   * @return */
  private int steps(Scalar minResolution) {
    return Ceiling.intValueExact(length().divide(Sign.requirePositive(minResolution)));
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    int n = steps(minResolution);
    if (n == 0)
      return Tensors.of(end());
    return Tensor.of(Subdivide.of(0.0, 1.0, n).stream() //
        .skip(1) //
        .map(Scalar.class::cast) //
        .map(dubinsPath.unit(start())));
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    int steps = steps(minResolution);
    return new TransitionWrap( //
        sampled(minResolution), //
        ConstantArray.of(length().divide(RealScalar.of(steps)), steps));
  }

  @Override // from RenderTransition
  public Tensor linearized(Scalar minResolution) {
    // TODO JPH check if this works with units
    if (dubinsPath.type().containsStraight() && //
        Scalars.lessThan(minResolution, dubinsPath.length(1))) {
      ScalarTensorFunction scalarTensorFunction = dubinsPath.unit(start());
      int s0 = Ceiling.intValueExact(dubinsPath.length(0).divide(minResolution));
      int s2 = Ceiling.intValueExact(dubinsPath.length(2).divide(minResolution));
      Tensor segments = dubinsPath.segments().divide(length());
      Tensor p0 = s0 == 0 ? Tensors.empty() : Subdivide.of(segments.Get(0).zero(), segments.Get(0), s0);
      Tensor p2 = s2 == 0 ? Tensors.empty() : Subdivide.of(segments.Get(1), segments.Get(2), s2);
      return Join.of(p0, p2).map(scalarTensorFunction);
    }
    int n = Math.max(1, steps(minResolution));
    return Subdivide.of(0.0, 1.0, n) //
        .map(dubinsPath.unit(start()));
  }
}
