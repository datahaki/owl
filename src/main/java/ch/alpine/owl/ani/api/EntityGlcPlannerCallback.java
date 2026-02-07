// code by jph
package ch.alpine.owl.ani.api;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.owl.glc.adapter.GlcTrajectories;
import ch.alpine.owl.glc.adapter.Trajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.math.state.TrajectorySample;

public class EntityGlcPlannerCallback implements GlcPlannerCallback {
  public static GlcPlannerCallback of(TrajectoryEntity trajectoryEntity) {
    return new EntityGlcPlannerCallback(trajectoryEntity, false);
  }

  public static GlcPlannerCallback verbose(TrajectoryEntity trajectoryEntity) {
    return new EntityGlcPlannerCallback(trajectoryEntity, true);
  }

  // ---
  private final TrajectoryEntity trajectoryEntity;
  private final boolean showCost;

  private EntityGlcPlannerCallback(TrajectoryEntity trajectoryEntity, boolean showCost) {
    this.trajectoryEntity = Objects.requireNonNull(trajectoryEntity);
    this.showCost = showCost;
  }

  @Override // from GlcPlannerCallback
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      if (showCost)
        System.out.println("Cost to Goal: " + optional.orElseThrow().costFromRoot());
      List<TrajectorySample> tail = //
          GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.orElseThrow());
      trajectoryEntity.trajectory(Trajectories.glue(head, tail));
    }
  }
}
