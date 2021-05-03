// code by jph
package ch.alpine.owl.bot.delta;

import java.io.Serializable;
import java.util.Optional;

import ch.alpine.owl.ani.api.EntityControl;
import ch.alpine.owl.ani.api.ProviderRank;
import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2Norm;

/* package */ class DeltaCoastingControl implements EntityControl, Serializable {
  private final ImageGradientInterpolation imageGradientInterpolation;
  private final Scalar u_norm;

  public DeltaCoastingControl(ImageGradientInterpolation imageGradientInterpolation, Scalar u_norm) {
    this.imageGradientInterpolation = imageGradientInterpolation;
    this.u_norm = u_norm;
  }

  @Override // from EntityControl
  public Optional<Tensor> control(StateTime tail, Scalar now) {
    Tensor u = imageGradientInterpolation.get(tail.state());
    Scalar norm = Vector2Norm.of(u);
    if (Scalars.lessThan(u_norm, norm))
      u = u.multiply(u_norm).divide(norm);
    return Optional.of(u.negate());
  }

  @Override // from EntityControl
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }
}
