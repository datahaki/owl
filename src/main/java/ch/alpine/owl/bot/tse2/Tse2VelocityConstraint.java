// code by ynager
package ch.alpine.owl.bot.tse2;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** Velocity constraint for {@link Tse2StateSpaceModel}
 * 
 * this constraint is obsolete when using the {@link Tse2Integrator}
 * which already bounds the velocity */
/* package */ class Tse2VelocityConstraint implements PlannerConstraint, Serializable {
  private final Clip clip;

  public Tse2VelocityConstraint(Clip clip) {
    this.clip = clip;
  }

  /** @param min allowed velocity
   * @param max allowed velocity greater equals min
   * @throws Exception if min exceeds max */
  public Tse2VelocityConstraint(Scalar min, Scalar max) {
    this(Clips.interval(min, max));
  }

  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return trajectory.stream() //
        .map(StateTime::state) //
        .map(Tse2StateSpaceModel.STATE_VELOCITY) //
        .allMatch(clip::isInside);
  }
}
