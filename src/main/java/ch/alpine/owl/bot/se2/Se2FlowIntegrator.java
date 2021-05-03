// code by jph
package ch.alpine.owl.bot.se2;

import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.sophus.lie.se2.Se2Integrator;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** exact integration of flow using matrix exponential and logarithm.
 * states are encoded in the default coordinates of the se2 Lie-algebra. */
public enum Se2FlowIntegrator implements Integrator {
  INSTANCE;

  /** Parameter description:
   * g in SE2
   * h in R */
  @Override // from Integrator
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor g, Tensor u, Scalar h) {
    return Se2Integrator.INSTANCE.spin(g, u.multiply(h));
  }
}
