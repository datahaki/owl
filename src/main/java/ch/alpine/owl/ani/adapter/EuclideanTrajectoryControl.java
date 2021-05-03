// code by jph
package ch.alpine.owl.ani.adapter;

import java.util.List;
import java.util.Optional;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2NormSquared;

public final class EuclideanTrajectoryControl extends StateTrajectoryControl {
  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return Vector2NormSquared.between(x, y);
  }

  @Override // from StateTrajectoryControl
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    return Optional.empty();
  }
}
