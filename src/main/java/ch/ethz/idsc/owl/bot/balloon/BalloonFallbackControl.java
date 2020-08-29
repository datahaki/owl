// code by jph, astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.EntityControl;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum BalloonFallbackControl implements EntityControl {
  INSTANCE;

  @Override
  public Optional<Tensor> control(StateTime tail, Scalar now) {
    return Optional.of(Tensors.vector(0));
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }
}
