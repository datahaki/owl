// code by astoll, ynager
package ch.alpine.owl.glc.rl2;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.VectorScalars;
import ch.alpine.owl.math.order.EboTracker;
import ch.alpine.owl.math.order.SetEboTracker;
import ch.alpine.tensor.Tensor;

/** holds the node which have not yet been expanded */
/* package */ class RelaxedGlobalQueue extends RelaxedPollingQueue {
  private final Tensor slacks;

  /** @param slacks */
  public RelaxedGlobalQueue(Tensor slacks) {
    this.slacks = slacks;
  }

  /** Adds single node to global queue. */
  @Override // from RelaxedPriorityQueue
  public Collection<GlcNode> add(GlcNode glcNode) {
    addSingle(glcNode);
    return Collections.emptyList();
  }

  @Override // from RelaxedPriorityQueue
  public GlcNode peekBest() {
    EboTracker<GlcNode> eboTracker = SetEboTracker.withList(slacks);
    Iterator<GlcNode> iterator = iterator();
    while (iterator.hasNext()) {
      GlcNode currentGlcNode = iterator.next();
      eboTracker.digest(currentGlcNode, VectorScalars.vector(currentGlcNode.merit()));
    }
    return eboTracker.peekBestKey();
  }

  @Override // from RelaxedPollingQueue
  protected GlcNode getNodeToPoll() {
    return peekBest();
  }
}
