// code by jph
package ch.alpine.owl.gui.win;

import java.util.List;

import ch.alpine.owl.ani.api.AnimationInterface;
import ch.alpine.owl.ani.api.RrtsPlannerCallback;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.owl.rrts.core.TransitionPlanner;

@Deprecated
class DefaultTrajectoryPlannerCallback implements RrtsPlannerCallback {
  public AnimationInterface controllable;

  @Override
  public void expandResult(List<TrajectorySample> head, TransitionPlanner trajectoryPlanner) {
    // ---
  }
  /* @Override
   * public void expandResult(List<TrajectorySample> head, RrtsPlanner rrtsPlanner, List<TrajectorySample> tail) {
   * List<TrajectorySample> trajectory = new ArrayList<>();
   * if (controllable instanceof TrajectoryEntity) {
   * TrajectoryEntity abstractEntity = (TrajectoryEntity) controllable;
   * trajectory = Trajectories.glue(head, tail);
   * abstractEntity.trajectory(trajectory);
   * }
   * // trajectoryRender.setTrajectory(trajectory);
   * if (rrtsPlanner.getBest().isPresent()) {
   * RrtsNode root = Nodes.rootFrom(rrtsPlanner.getBest().get());
   * // Collection<RrtsNode> collection =
   * Nodes.ofSubtree(root);
   * // treeRender.setCollection(collection);
   * }
   * } */
}
