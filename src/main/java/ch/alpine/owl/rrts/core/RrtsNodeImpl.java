// code by jph
package ch.alpine.owl.rrts.core;

import ch.alpine.owl.data.tree.SetNode;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;

/** Implementation based on
 * Sertac Karaman and Emilio Frazzoli, 2011:
 * Sampling-based algorithms for optimal motion planning
 * Algorithm 6, p.855 */
/* package */ class RrtsNodeImpl extends SetNode<RrtsNode> implements RrtsNode {
  private final Tensor state;
  private Scalar costFromRoot;

  /* package */ RrtsNodeImpl(Tensor state, Scalar costFromRoot) {
    this.state = state;
    this.costFromRoot = costFromRoot;
  }

  @Override
  public RrtsNode connectTo(Tensor state, Scalar costFromRoot) {
    RrtsNode leaf = new RrtsNodeImpl(state, costFromRoot);
    this.insertEdgeTo(leaf);
    return leaf;
  }

  @Override
  public void rewireTo(RrtsNode child, Scalar costFromParent) {
    child.parent().removeEdgeTo(child);
    insertEdgeTo(child);
    final Scalar nodeCostFromRoot = costFromRoot().add(costFromParent);
    // the condition of cost reduction is not strictly necessary
    if (!Scalars.lessThan(nodeCostFromRoot, child.costFromRoot()))
      throw new Throw(nodeCostFromRoot, child.costFromRoot());
    _propagate(child, nodeCostFromRoot);
    ((RrtsNodeImpl) child).costFromRoot = nodeCostFromRoot;
  }

  private static void _propagate(RrtsNode node, Scalar nodeCostFromRoot) {
    for (RrtsNode _child : node.children()) {
      RrtsNodeImpl child = (RrtsNodeImpl) _child;
      Scalar costFromParent = child.costFromRoot().subtract(node.costFromRoot());
      Scalar newCostFromRoot = nodeCostFromRoot.add(costFromParent);
      _propagate(child, newCostFromRoot);
      child.costFromRoot = newCostFromRoot;
    }
  }

  @Override
  public Tensor state() {
    return state;
  }

  @Override
  public Scalar costFromRoot() {
    return costFromRoot;
  }
}
