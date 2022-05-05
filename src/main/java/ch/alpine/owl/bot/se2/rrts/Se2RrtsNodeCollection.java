// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.alpine.bridge.util.BoundedSortedQueue;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.RrtsNodeTransition;
import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.NdCenters;
import ch.alpine.tensor.opt.nd.NdCollectNearest;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;

public class Se2RrtsNodeCollection implements RrtsNodeCollection {
  private final TransitionSpace transitionSpace;
  private final NdMap<RrtsNode> ndMap;
  private final int factor;

  public Se2RrtsNodeCollection(TransitionSpace transitionSpace, CoordinateBoundingBox box, int factor) {
    this.transitionSpace = Objects.requireNonNull(transitionSpace);
    Integers.requireEquals(box.dimensions(), 2);
    ndMap = NdTreeMap.of(box);
    this.factor = factor;
  }

  @Override
  public void insert(RrtsNode rrtsNode) {
    ndMap.insert(rrtsNode.state().extract(0, 2), rrtsNode);
  }

  @Override
  public int size() {
    return ndMap.size();
  }

  @Override
  public Collection<RrtsNodeTransition> nearTo(Tensor tail, int k_nearest) {
    Collection<NdMatch<RrtsNode>> collection = //
        NdCollectNearest.of(ndMap, NdCenters.VECTOR_2_NORM.apply(tail.extract(0, 2)), k_nearest * factor);
    BoundedSortedQueue<Scalar, RrtsNodeTransition> boundedMinQueue = BoundedSortedQueue.min(k_nearest);
    // ---
    for (NdMatch<RrtsNode> ndMatch : collection) {
      RrtsNode rrtsNode = ndMatch.value();
      Transition transition = transitionSpace.connect(rrtsNode.state(), tail);
      boundedMinQueue.offer(transition.length(), new RrtsNodeTransition(rrtsNode, transition));
    }
    // ---
    return boundedMinQueue.values().collect(Collectors.toSet());
  }

  @Override
  public Collection<RrtsNodeTransition> nearFrom(Tensor head, int k_nearest) {
    Collection<NdMatch<RrtsNode>> collection = //
        NdCollectNearest.of(ndMap, NdCenters.VECTOR_2_NORM.apply(head.extract(0, 2)), k_nearest * factor);
    BoundedSortedQueue<Scalar, RrtsNodeTransition> boundedMinQueue = BoundedSortedQueue.min(k_nearest);
    // ---
    for (NdMatch<RrtsNode> ndMatch : collection) {
      RrtsNode rrtsNode = ndMatch.value();
      Transition transition = transitionSpace.connect(head, rrtsNode.state());
      boundedMinQueue.offer(transition.length(), new RrtsNodeTransition(rrtsNode, transition));
    }
    // ---
    return boundedMinQueue.values().collect(Collectors.toSet());
  }
}
