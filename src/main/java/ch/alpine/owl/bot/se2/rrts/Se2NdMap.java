// code by jph, gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.util.Collection;
import java.util.Queue;
import java.util.function.Function;

import ch.alpine.java.util.BoundedPriorityQueue;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.clt.ClothoidComparators;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.opt.nd.NdCenters;
import ch.alpine.tensor.opt.nd.NdCollectNearest;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;

public class Se2NdMap<T> {
  private final NdMap<T> ndMap;
  private final Function<T, Tensor> se2Projection;

  public Se2NdMap(NdBox ndBox, Function<T, Tensor> se2Projection) {
    ndMap = NdTreeMap.of(ndBox);
    this.se2Projection = se2Projection;
  }

  public void insert(T value) {
    ndMap.insert(se2Projection.apply(value).extract(0, 2), value);
  }

  public Collection<Clothoid> nearFrom(T value, int limit) {
    Tensor origin = se2Projection.apply(value);
    Collection<NdMatch<T>> collection = //
        NdCollectNearest.of(ndMap, NdCenters.VECTOR_2_NORM.apply(origin.extract(0, 2)), limit * 3);
    ClothoidBuilder clothoidBuilder = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
    Queue<Clothoid> queue = BoundedPriorityQueue.min(limit, ClothoidComparators.LENGTH);
    for (NdMatch<T> ndMatch : collection)
      queue.add(clothoidBuilder.curve(origin, se2Projection.apply(ndMatch.value())));
    return queue;
  }

  public Collection<Clothoid> nearTo(T value, int limit) {
    Tensor target = se2Projection.apply(value);
    Collection<NdMatch<T>> collection = //
        NdCollectNearest.of(ndMap, NdCenters.VECTOR_2_NORM.apply(target.extract(0, 2)), limit * 3);
    ClothoidBuilder clothoidBuilder = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
    Queue<Clothoid> queue = BoundedPriorityQueue.min(limit, ClothoidComparators.LENGTH);
    for (NdMatch<T> ndMatch : collection)
      queue.add(clothoidBuilder.curve(se2Projection.apply(ndMatch.value()), target));
    return queue;
  }
}
