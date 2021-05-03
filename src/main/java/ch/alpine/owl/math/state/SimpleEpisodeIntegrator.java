// code by jph
package ch.alpine.owl.math.state;

import java.util.List;

import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** {@link SimpleEpisodeIntegrator} takes the largest possible time step for integration.
 * 
 * implementation is fast and should only be applied for simple {@link StateSpaceModel}s */
public class SimpleEpisodeIntegrator extends AbstractEpisodeIntegrator {
  public SimpleEpisodeIntegrator(StateSpaceModel stateSpaceModel, Integrator integrator, StateTime stateTime) {
    super(stateSpaceModel, integrator, stateTime);
  }

  @Override // from AbstractEpisodeIntegrator
  protected List<StateTime> abstract_move(Tensor flow, Scalar period) {
    return FixedStateIntegrator.create(integrator, stateSpaceModel, period, 1).trajectory(tail(), flow);
  }
}
