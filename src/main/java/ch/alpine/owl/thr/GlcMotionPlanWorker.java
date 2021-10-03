// code by jph, gjoel
package ch.alpine.owl.thr;

import java.util.Collection;

import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.core.TrajectoryPlanner;

/* package */ class GlcMotionPlanWorker extends MotionPlanWorker<TrajectoryPlanner, GlcPlannerCallback> {
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
