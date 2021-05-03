// code by jph, astoll
package ch.alpine.owl.bot.balloon;

import java.util.Optional;

import ch.alpine.owl.ani.api.EntityControl;
import ch.alpine.owl.ani.api.ProviderRank;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

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
