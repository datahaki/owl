// code by jph
package ch.alpine.owl.bot.tse2;

import java.io.Serializable;
import java.util.Optional;

import ch.alpine.owl.ani.api.EntityControl;
import ch.alpine.owl.ani.api.ProviderRank;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Sign;

public class Tse2FallbackControl implements EntityControl, Serializable {
  private final Scalar magnitude;

  public Tse2FallbackControl(Scalar magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public Optional<Tensor> control(StateTime tail, Scalar now) {
    Scalar vx = tail.state().Get(Tse2StateSpaceModel.STATE_INDEX_VEL);
    return Optional.of(Tensors.of(RealScalar.ZERO, Sign.FUNCTION.apply(vx).multiply(magnitude).negate()));
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }
}
