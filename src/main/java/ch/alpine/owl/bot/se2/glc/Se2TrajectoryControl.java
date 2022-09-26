// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.ani.adapter.StateTrajectoryControl;
import ch.alpine.owl.bot.se2.Se2Wrap;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.sca.Clip;

/* package */ abstract class Se2TrajectoryControl extends StateTrajectoryControl implements TrajectoryTargetRender {
  // ---
  protected final Clip clip;

  protected Se2TrajectoryControl(Clip clip) {
    this.clip = clip;
  }

  @Override // from StateTrajectoryControl
  protected final Scalar pseudoDistance(Tensor x, Tensor y) {
    return Vector2NormSquared.of(Se2Wrap.INSTANCE.difference(x, y));
  }
}
