// code by jph, gjoel
package ch.alpine.owl.ani.api;

import java.util.Collection;

import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;

class GlcMotionPlanWorker extends MotionPlanWorker<TrajectoryPlanner, GlcPlannerCallback> {
  public GlcMotionPlanWorker(int maxSteps, Collection<GlcPlannerCallback> glcPlannerCallbacks) {
    super(maxSteps, glcPlannerCallbacks);
  }

  @Override
  protected void expand(TrajectoryPlanner trajectoryPlanner) {
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.setContinued(() -> isRelevant);
    glcExpand.findAny(maxSteps);
  }
}
