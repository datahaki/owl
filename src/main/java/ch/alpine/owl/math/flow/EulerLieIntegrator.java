// code by jph
package ch.alpine.owl.math.flow;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.LieIntegrator;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public class EulerLieIntegrator implements Integrator, LieIntegrator, Serializable {
  /** @param lieGroup
   * @return */
  public static Integrator of(LieGroup lieGroup) {
    return new EulerLieIntegrator(Objects.requireNonNull(lieGroup));
  }

  // ---
  private final LieGroup lieGroup;

  private EulerLieIntegrator(LieGroup lieGroup) {
    this.lieGroup = lieGroup;
  }

  @Override // from Integrator
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
    return spin(x, stateSpaceModel.f(x, u).multiply(h));
  }

  @Override // from LieIntegrator
  public Tensor spin(Tensor g, Tensor v) {
    return lieGroup.element(g).combine(lieGroup.exp(v));
  }
}
