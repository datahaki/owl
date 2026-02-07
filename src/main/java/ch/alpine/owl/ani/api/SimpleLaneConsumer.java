// code by gjoel
package ch.alpine.owl.ani.api;

import java.util.Collection;

import ch.alpine.owl.data.tree.TreePlanner;
import ch.alpine.owl.glc.adapter.GoalConsumer;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.lane.LaneConsumer;
import ch.alpine.owl.lane.LaneInterface;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Last;

public class SimpleLaneConsumer implements LaneConsumer {
  private final TrajectoryEntity trajectoryEntity;
  private final PlannerConstraint plannerConstraint;
  private final GoalConsumer goalConsumer;

  public SimpleLaneConsumer( //
      TrajectoryEntity trajectoryEntity, //
      PlannerConstraint plannerConstraint, //
      Collection<? extends PlannerCallback> plannerCallbacks) {
    this.trajectoryEntity = trajectoryEntity;
    this.plannerConstraint = plannerConstraint;
    goalConsumer = new SimpleGoalConsumer(trajectoryEntity, plannerConstraint, plannerCallbacks);
  }

  @Override // from Consumer
  public void accept(LaneInterface lane) {
    Tensor goal = Last.of(lane.midLane());
    TreePlanner treePlanner = trajectoryEntity.createTreePlanner(plannerConstraint, goal);
    if (treePlanner instanceof LaneConsumer laneConsumer)
      laneConsumer.accept(lane);
    goalConsumer.accept(goal);
  }
}
