// code by jph
package ch.alpine.owl.ani.api;

import java.util.Optional;

import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public interface EntityControl {
  /** @param tail last simulated or estimated state of entity
   * @param now time
   * @return control input to {@link EpisodeIntegrator} */
  Optional<Tensor> control(StateTime tail, Scalar now);

  /** @return rank of this control provider */
  ProviderRank getProviderRank();
}
