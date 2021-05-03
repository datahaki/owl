// code by astoll
package ch.alpine.owl.bot.balloon;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

/* package */ class BalloonPlannerConstraint implements PlannerConstraint, Serializable {
  /** constants of the hot air balloon */
  private final Clip vertSpeed_clip;

  public BalloonPlannerConstraint(Scalar vertSpeedMax) {
    vertSpeed_clip = Clips.absolute(vertSpeedMax);
  }

  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    Tensor state = glcNode.state();
    /* altitude no less than 0 */
    Scalar y = state.Get(1);
    if (Sign.isNegative(y))
      return false;
    /* vertical speed within given interval */
    Scalar v = state.Get(2);
    if (vertSpeed_clip.isOutside(v))
      return false;
    return true;//
  }
}
