// code by jph
package ch.alpine.owl.bot.se2.twd;

import java.util.List;
import java.util.Optional;

import ch.alpine.owl.ani.adapter.StateTrajectoryControl;
import ch.alpine.owl.bot.se2.Se2Wrap;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2NormSquared;

public final class TwdTrajectoryControl extends StateTrajectoryControl {
  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return Vector2NormSquared.of(Se2Wrap.INSTANCE.difference(x, y));
  }

  @Override // from StateTrajectoryControl
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    return Optional.empty();
  }
}
