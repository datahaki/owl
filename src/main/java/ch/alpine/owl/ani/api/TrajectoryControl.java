// code by jph
package ch.alpine.owl.ani.api;

import java.util.List;

import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TrajectorySample;
import ch.alpine.tensor.Scalar;

public interface TrajectoryControl extends EntityControl, TrajectoryListener {
  /** @param delay
   * @return trajectory until delay[s] in the future of entity,
   * or current position if entity does not have a trajectory */
  List<TrajectorySample> getFutureTrajectoryUntil(StateTime tail, Scalar delay);
}
