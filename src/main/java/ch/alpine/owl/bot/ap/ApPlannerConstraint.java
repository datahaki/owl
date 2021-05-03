// code by astoll
package ch.alpine.owl.bot.ap;

import java.util.List;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sin;

/** ApPlannerConstraint sets the constraints for the airplane simulation.
 * 
 * x and z need to be positive,
 * the velocity shall not be smaller than stall speed and not greater than the maximum speed,
 * the flight path angle (gamma) should be no less than -3 degree and always negative
 * the descent rate should be z_dot <= V * sin(gamma) in final landing phase
 * 
 * The values always are to be found in {@link ApStateSpaceModel} */
/* package */ enum ApPlannerConstraint implements PlannerConstraint {
  INSTANCE;

  private static final Clip CLIP_GAMMA = //
      Clips.interval(ApStateSpaceModel.MAX_DESCENT_GAMMA, ApStateSpaceModel.MAX_DESCENT_GAMMA.zero());
  private static final Clip CLIP_VELOCITY = //
      Clips.interval(ApStateSpaceModel.STALL_SPEED, ApStateSpaceModel.MAX_SPEED);

  @Override // from PlannerConstraint
  public final boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    Tensor state = glcNode.state();
    // boolean xConstraint = Sign.isPositiveOrZero(state.Get(0));
    // if (!xConstraint)
    // return false;
    Scalar z = state.Get(1);
    if (Sign.isNegative(z))
      return false;
    Scalar v = state.Get(2);
    if (CLIP_VELOCITY.isOutside(v))
      return false;
    Scalar gamma = state.Get(3); // flight path angle
    if (CLIP_GAMMA.isOutside(gamma))
      return false;
    if (Scalars.lessEquals(z, ApStateSpaceModel.ALTITUDE_FINAL_PHASE)) {
      Scalar v_z = v.multiply(Sin.of(gamma));
      return Scalars.lessEquals(ApStateSpaceModel.Z_DOT_0_MAX, v_z);
    }
    return true;//
  }
}
