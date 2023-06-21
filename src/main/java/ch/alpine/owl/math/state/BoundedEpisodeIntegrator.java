// code by jph
package ch.alpine.owl.math.state;

import java.util.List;

import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Sign;

/** integrates along given flow with time steps that do not exceed a predefined threshold */
public class BoundedEpisodeIntegrator extends AbstractEpisodeIntegrator {
  private final Scalar maxStep;

  /** @param stateSpaceModel
   * @param integrator
   * @param stateTime initial state
   * @param maxStep in time that given integrator applies */
  public BoundedEpisodeIntegrator(StateSpaceModel stateSpaceModel, Integrator integrator, StateTime stateTime, Scalar maxStep) {
    super(stateSpaceModel, integrator, stateTime);
    this.maxStep = Sign.requirePositive(maxStep);
  }

  @Override // from AbstractEpisodeIntegrator
  protected List<StateTime> abstract_move(Tensor flow, Scalar period) {
    Scalar steps = Ceiling.FUNCTION.apply(period.divide(maxStep));
    return FixedStateIntegrator.create( //
        integrator, stateSpaceModel, period.divide(steps), Scalars.intValueExact(steps)).trajectory(tail(), flow);
  }
}
