// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.util.List;
import java.util.Optional;

import ch.alpine.owl.ani.adapter.StateTrajectoryControl;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.nrm.Vector2NormSquared;

public class R2TrajectoryControl extends StateTrajectoryControl {
  private static final Scalar THRESHOLD = RealScalar.of(0.2);

  @Override
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Tensor state = tail.state();
    for (TrajectorySample trajectorySample : trailAhead) {
      Tensor diff = trajectorySample.stateTime().state().subtract(state);
      if (Scalars.lessThan(THRESHOLD, Vector2Norm.of(diff)))
        return Optional.of(Vector2Norm.NORMALIZE.apply(diff));
    }
    // System.out.println("fail custom control");
    return Optional.empty();
  }

  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return Vector2NormSquared.between(x, y);
  }
}
