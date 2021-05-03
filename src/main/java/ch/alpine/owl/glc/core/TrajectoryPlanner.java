// code by ynager
package ch.alpine.owl.glc.core;

import java.util.Map;

import ch.alpine.owl.data.tree.TreePlanner;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.tensor.Tensor;

public interface TrajectoryPlanner extends TreePlanner<GlcNode> {
  /** @return state integrator for the state space to generate trajectories from given controls */
  StateIntegrator getStateIntegrator();

  /** @return goal query for the purpose of inspection */
  HeuristicFunction getHeuristicFunction();

  /** @return unmodifiable view on domain map for display and tests */
  Map<Tensor, GlcNode> getDomainMap();
}
