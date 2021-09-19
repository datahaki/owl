// code by jph
package ch.alpine.owl.bot.rn.rrts;

import java.util.Collection;
import java.util.stream.Collectors;

import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.EuclideanNdCenter;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;
import ch.alpine.tensor.opt.nd.NearestNdCluster;

public final class RnRrtsNodeCollection implements RrtsNodeCollection {
  private final NdMap<RrtsNode> ndMap;

  /** @param lbounds vector
   * @param ubounds vector */
  public RnRrtsNodeCollection(Tensor lbounds, Tensor ubounds) {
    ndMap = new NdTreeMap<>(lbounds, ubounds, 5, 20); // magic const
  }

  @Override // from RrtsNodeCollection
  public void insert(RrtsNode rrtsNode) {
    ndMap.add(rrtsNode.state(), rrtsNode);
  }

  @Override // from RrtsNodeCollection
  public int size() {
    return ndMap.size();
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearTo(Tensor end, int k_nearest) {
    Collection<NdMatch<RrtsNode>> collection = // 
        NearestNdCluster.of(ndMap, EuclideanNdCenter.of(end), k_nearest);
    return collection.stream() //
        .map(NdMatch::value) //
        .collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    return nearTo(start, k_nearest);
  }
}
