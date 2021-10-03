// code by jph
package ch.alpine.owl.glc.adapter;

import java.util.Collection;
import java.util.Objects;

import ch.alpine.owl.ani.api.PlannerCallback;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.thr.MotionPlanWorker;
import ch.alpine.tensor.Tensor;

public class SimpleGoalConsumer implements GoalConsumer {
  private static final int MAX_STEPS = 5000; // magic const
  // ---
  private final TrajectoryEntity trajectoryEntity;
  private final PlannerConstraint plannerConstraint;
  private final Collection<? extends PlannerCallback> plannerCallbacks;
  // ---
  private MotionPlanWorker motionPlanWorker = null;

  public SimpleGoalConsumer(TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, Collection<? extends PlannerCallback> plannerCallbacks) {
    this.trajectoryEntity = trajectoryEntity;
    this.plannerConstraint = plannerConstraint;
    this.plannerCallbacks = plannerCallbacks;
  }

  @Override // from GoalConsumer
  @SuppressWarnings("unchecked")
  public void accept(Tensor goal) {
    if (Objects.nonNull(motionPlanWorker)) {
      motionPlanWorker.flagShutdown();
      motionPlanWorker = null;
    }
    motionPlanWorker = MotionPlanWorker.of(MAX_STEPS, plannerCallbacks);
    motionPlanWorker.start( //
        trajectoryEntity.getFutureTrajectoryUntil(trajectoryEntity.delayHint()), //
        trajectoryEntity.createTreePlanner(plannerConstraint, goal));
  }
}
