// code by jph
package ch.alpine.owl.rrts.adapter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.alpine.java.util.BoundedSortedQueue;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.RrtsNodeTransition;
import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** Careful: implementation is only for testing purposes
 * 
 * implementation performs a complete sweep through the collection
 * the runtime is prohibitive */
public class ExhaustiveRrtsNodeCollection implements RrtsNodeCollection {
  /** @param transitionSpace non-null
   * @return */
  public static RrtsNodeCollection of(TransitionSpace transitionSpace) {
    return new ExhaustiveRrtsNodeCollection(Objects.requireNonNull(transitionSpace));
  }

  // ---
  private final Collection<RrtsNode> collection = new LinkedList<>();
  private final TransitionSpace transitionSpace;

  private ExhaustiveRrtsNodeCollection(TransitionSpace transitionSpace) {
    this.transitionSpace = transitionSpace;
  }

  @Override // from RrtsNodeCollection
  public void insert(RrtsNode rrtsNode) {
    collection.add(rrtsNode);
  }

  @Override // from RrtsNodeCollection
  public int size() {
    return collection.size();
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNodeTransition> nearTo(Tensor tail, int k_nearest) {
    BoundedSortedQueue<Scalar, RrtsNodeTransition> queue = BoundedSortedQueue.min(k_nearest);
    for (RrtsNode rrtsNode : collection) {
      Transition transition = transitionSpace.connect(rrtsNode.state(), tail);
      queue.offer(transition.length(), new RrtsNodeTransition(rrtsNode, transition));
    }
    return queue.values().collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNodeTransition> nearFrom(Tensor head, int k_nearest) {
    BoundedSortedQueue<Scalar, RrtsNodeTransition> queue = BoundedSortedQueue.min(k_nearest);
    for (RrtsNode rrtsNode : collection) {
      Transition transition = transitionSpace.connect(head, rrtsNode.state());
      queue.offer(transition.length(), new RrtsNodeTransition(rrtsNode, transition));
    }
    return queue.values().collect(Collectors.toList());
  }
}
