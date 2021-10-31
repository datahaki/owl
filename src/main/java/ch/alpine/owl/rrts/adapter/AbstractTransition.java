// code by jph
package ch.alpine.owl.rrts.adapter;

import java.io.Serializable;

import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** suggested base class for all implementations of {@link Transition} */
public abstract class AbstractTransition implements Transition, Serializable {
  private final Tensor start;
  private final Tensor end;
  private final Scalar length;

  public AbstractTransition(Tensor start, Tensor end, Scalar length) {
    this.start = start;
    this.end = end;
    this.length = length;
  }

  @Override // from Transition
  public final Tensor start() {
    return start;
  }

  @Override // from Transition
  public final Tensor end() {
    return end;
  }

  @Override // from Transition
  public final Scalar length() {
    return length;
  }
}
