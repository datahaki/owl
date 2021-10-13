// code by jph and jl
package ch.alpine.owl.glc.core;

import java.util.Collection;

import ch.alpine.owl.data.tree.StateCostNode;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** glc specific node
 * 
 * immutable except for children, parent, and depth which are only modified in
 * {@link GlcNode#addChild(GlcNode)} */
public interface GlcNode extends StateCostNode {
  /** creates {@link GlcNode} without parent and without descendants/children
   * 
   * @param flow used to reach this node, or null if this node is root
   * @param stateTime
   * @param costFromRoot exact accumulation of costs along edges from root to this node
   * @param minCostToGoal lower bound for remaining costs to reach the goal region from given stateTime
   * @return */
  static GlcNode of(Tensor flow, StateTime stateTime, Scalar costFromRoot, Scalar minCostToGoal) {
    return new GlcNodeImpl(flow, stateTime, costFromRoot, minCostToGoal);
  }

  // ---
  @Override // from Node
  GlcNode parent();

  /** @return flow between parent and this node. if this node is root, flow == null */
  Tensor flow();

  /** @return coordinate in space-time of node */
  StateTime stateTime();

  /** @return cost from root plus min cost to goal */
  Scalar merit();

  @Override // from StateCostNode
  Collection<GlcNode> children();
}
