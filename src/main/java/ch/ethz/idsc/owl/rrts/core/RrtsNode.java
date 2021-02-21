// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;

import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface RrtsNode extends StateCostNode {
  /** @param state non-null
   * @param cost non-null
   * @return new node holding state and cost with parent == null
   * @throws Exception if either of the input parameters is null */
  static RrtsNode createRoot(Tensor state, Scalar cost) {
    return new RrtsNodeImpl( //
        Objects.requireNonNull(state), //
        Objects.requireNonNull(cost));
  }

  @Override // from Node
  RrtsNode parent();

  @Override // from Node
  Collection<? extends RrtsNode> children();

  /** @param state
   * @param costFromRoot
   * @return new node holding state and cost with parent == this */
  RrtsNode connectTo(Tensor state, Scalar costFromRoot);

  /** replace parent of given child with this as new parent, and update all
   * costs in the subtree of child.
   * 
   * In particular, the {@link #costFromRoot()} of this node do not change.
   * 
   * @param child
   * @param edgeCost calculator between two nodes
   * @param influence region of influence for cost */
  // TODO JPH influence is obsolete, since edgeCost has access
  void rewireTo(RrtsNode child, BiFunction<RrtsNode, RrtsNode, Scalar> edgeCost, int influence);
}
