// code by jph
package ch.alpine.owl.math.pursuit;

import java.util.Optional;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public interface PursuitInterface {
  /** @return first/current turning ratio required to drive the calculated curve */
  Optional<Scalar> firstRatio();

  /** @return */
  Tensor ratios();
}
