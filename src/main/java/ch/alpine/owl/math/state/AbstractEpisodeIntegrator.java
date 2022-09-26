// code by jph
package ch.alpine.owl.math.state;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Lists;

/* package */ abstract class AbstractEpisodeIntegrator implements EpisodeIntegrator, Serializable {
  protected final StateSpaceModel stateSpaceModel;
  /* package */ final Integrator integrator;
  private StateTime stateTime;

  protected AbstractEpisodeIntegrator(StateSpaceModel stateSpaceModel, Integrator integrator, StateTime stateTime) {
    this.stateSpaceModel = stateSpaceModel;
    this.integrator = integrator;
    this.stateTime = stateTime;
  }

  /** @param flow
   * @param period
   * @return */
  protected abstract List<StateTime> abstract_move(Tensor flow, Scalar period);

  @Override // from EpisodeIntegrator
  public final void move(Tensor u, Scalar now) {
    List<StateTime> trajectory = abstract_move(u, now.subtract(stateTime.time()));
    stateTime = Lists.last(trajectory);
  }

  @Override
  public final StateTime tail() {
    return stateTime;
  }
}
