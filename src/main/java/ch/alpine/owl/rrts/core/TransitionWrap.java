// code by gjoel
package ch.alpine.owl.rrts.core;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;

public class TransitionWrap {
  private final Tensor samples;
  private final Tensor spacing;

  public TransitionWrap(Tensor samples, Tensor spacing) {
    this.samples = samples.unmodifiable();
    this.spacing = VectorQ.requireLength(spacing.unmodifiable(), samples.length());
  }

  public Tensor samples() {
    return samples;
  }

  public Tensor spacing() {
    return spacing;
  }
}
