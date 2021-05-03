// code by jph, gjoel
package ch.alpine.owl.gui.win;

import java.util.Collection;
import java.util.Objects;

import ch.alpine.owl.ani.api.RrtsPlannerCallback;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.data.tree.Expand;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.TransitionPlanner;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.red.Min;

/* package */ class RrtsMotionPlanWorker extends MotionPlanWorker<TransitionPlanner, RrtsPlannerCallback> {
  private final Scalar delayHint;

  public RrtsMotionPlanWorker(int maxSteps, Collection<RrtsPlannerCallback> rrtsPlannerCallbacks) {
    super(maxSteps, rrtsPlannerCallbacks);
    delayHint = rrtsPlannerCallbacks.stream().filter(rrtsPlannerCallback -> rrtsPlannerCallback instanceof TrajectoryEntity) //
        .map(rrtsPlannerCallback -> ((TrajectoryEntity) rrtsPlannerCallback).delayHint()).reduce(Min::of).orElse(null);
  }

  @Override
  protected void expand(TransitionPlanner transitionPlanner) {
    Expand<RrtsNode> expand = new Expand<>(transitionPlanner);
    expand.setContinued(() -> isRelevant);
    if (Objects.nonNull(delayHint))
      expand.maxTime(delayHint);
    else
      expand.steps(maxSteps);
    transitionPlanner.checkConsistency();
  }
}
