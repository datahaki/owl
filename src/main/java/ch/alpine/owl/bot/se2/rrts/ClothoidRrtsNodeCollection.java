// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.util.Collection;
import java.util.stream.Collectors;

import ch.alpine.java.util.BoundedSortedQueue;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.opt.nd.NdCenters;
import ch.alpine.tensor.opt.nd.NdCollectNearest;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;

public class ClothoidRrtsNodeCollection implements RrtsNodeCollection {
  private static final int FACTOR = 3;
  // ---
  private final NdMap<RrtsNode> ndMap;

  public ClothoidRrtsNodeCollection(NdBox ndBox) {
    ndMap = NdTreeMap.of(ndBox);
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
  public Collection<RrtsNode> nearTo(Tensor tail, int k_nearest) {
    Collection<NdMatch<RrtsNode>> collection = //
        NdCollectNearest.of(ndMap, NdCenters.VECTOR_2_NORM.apply(tail.extract(0, 2)), k_nearest * FACTOR);
    BoundedSortedQueue<Scalar, RrtsNode> boundedMinQueue = BoundedSortedQueue.min(k_nearest);
    // ---
    ClothoidBuilder clothoidBuilder = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
    for (NdMatch<RrtsNode> ndMatch : collection) {
      RrtsNode rrtsNode = ndMatch.value();
      Clothoid clothoid = clothoidBuilder.curve(rrtsNode.state(), tail);
      boundedMinQueue.offer(clothoid.length(), rrtsNode);
    }
    // ---
    return boundedMinQueue.values().collect(Collectors.toSet());
  }

  @Override
  public Collection<RrtsNode> nearFrom(Tensor head, int k_nearest) {
    Collection<NdMatch<RrtsNode>> collection = //
        NdCollectNearest.of(ndMap, NdCenters.VECTOR_2_NORM.apply(head.extract(0, 2)), k_nearest * FACTOR);
    BoundedSortedQueue<Scalar, RrtsNode> boundedMinQueue = BoundedSortedQueue.min(k_nearest);
    // ---
    ClothoidBuilder clothoidBuilder = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
    for (NdMatch<RrtsNode> ndMatch : collection) {
      RrtsNode rrtsNode = ndMatch.value();
      Clothoid clothoid = clothoidBuilder.curve(head, rrtsNode.state());
      boundedMinQueue.offer(clothoid.length(), rrtsNode);
    }
    // ---
    return boundedMinQueue.values().collect(Collectors.toSet());
  }
}
