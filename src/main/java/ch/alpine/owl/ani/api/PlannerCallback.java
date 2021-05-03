// code by jph
package ch.alpine.owl.ani.api;

import java.util.List;

import ch.alpine.owl.data.tree.TreePlanner;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;

@FunctionalInterface
public interface PlannerCallback<T extends TreePlanner<?>> {
  /** @param head
   * @param treePlanner with a trajectory from the last {@link StateTime} in head */
  void expandResult(List<TrajectorySample> head, T treePlanner);
}
