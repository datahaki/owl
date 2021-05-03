// code by jph
package ch.alpine.owl.bot.psu;

import java.util.List;
import java.util.Optional;

import ch.alpine.owl.ani.adapter.StateTrajectoryControl;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/* package */ final class PsuTrajectoryControl extends StateTrajectoryControl {
  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return PsuMetric.INSTANCE.distance(x, y);
  }

  @Override // from StateTrajectoryControl
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    return Optional.empty();
  }
}
