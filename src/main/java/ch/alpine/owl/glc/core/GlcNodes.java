// code by jph
package ch.alpine.owl.glc.core;

import java.util.List;
import java.util.stream.Collectors;

import ch.alpine.owl.data.tree.Nodes;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;

public enum GlcNodes {
  ;
  /** @param stateTime
   * @param heuristicFunction
   * @return */
  public static GlcNode createRoot(StateTime stateTime, HeuristicFunction heuristicFunction) {
    Scalar minCost = heuristicFunction.minCostToGoal(stateTime.state());
    return GlcNode.of(null, stateTime, minCost.zero(), minCost);
  }

  /** coarse path of {@link StateTime}s of nodes from root to given node
   * 
   * @return path to goal if found, or path to current Node in queue
   * @throws Exception if node is null */
  public static List<StateTime> getPathFromRootTo(GlcNode node) {
    return Nodes.listFromRoot(node).stream() //
        .map(GlcNode::stateTime) //
        .collect(Collectors.toList());
  }
}
