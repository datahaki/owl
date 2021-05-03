// code by jph
package ch.alpine.owl.math.pursuit;

import java.util.Optional;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public enum VoidPursuit implements PursuitInterface {
  INSTANCE;

  @Override // from PursuitInterface
  public Optional<Scalar> firstRatio() {
    return Optional.empty();
  }

  @Override // from PursuitInterface
  public Tensor ratios() {
    return Tensors.empty();
  }
}
