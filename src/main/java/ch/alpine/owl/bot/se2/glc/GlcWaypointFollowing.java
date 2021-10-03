// code by ynager
package ch.alpine.owl.bot.se2.glc;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ch.alpine.owl.ani.api.MotionPlanWorker;
import ch.alpine.owl.ani.api.PlannerCallback;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public class GlcWaypointFollowing extends WaypointFollowing {
  private static final int MAX_STEPS = 5000; // magic const
  // ---
  protected final Collection<? extends PlannerCallback> plannerCallbacks;
  private MotionPlanWorker motionPlanWorker = null;
  private final PlannerConstraint plannerConstraint;

  /** @param waypoints
   * @param replanningRate
   * @param entity
   * @param plannerConstraint
   * @param plannerCallbacks */
  public GlcWaypointFollowing( //
      Tensor waypoints, Scalar replanningRate, TrajectoryEntity entity, //
      PlannerConstraint plannerConstraint, Collection<? extends PlannerCallback> plannerCallbacks) {
    super(waypoints, replanningRate, entity);
    this.plannerCallbacks = plannerCallbacks;
    this.plannerConstraint = plannerConstraint;
  }

  @Override // from WayPointFollowing
  @SuppressWarnings("unchecked")
  protected void planToGoal(List<TrajectorySample> head, Tensor goal) {
    if (Objects.nonNull(motionPlanWorker)) {
      motionPlanWorker.flagShutdown();
      motionPlanWorker = null;
    }
    motionPlanWorker = MotionPlanWorker.of(MAX_STEPS, plannerCallbacks);
    motionPlanWorker.start(head, entity.createTreePlanner(plannerConstraint, goal));
  }
}
