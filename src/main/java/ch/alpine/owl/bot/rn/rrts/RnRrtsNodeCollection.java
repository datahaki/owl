// code by jph
package ch.alpine.owl.bot.rn.rrts;

import java.util.Collection;
import java.util.stream.Collectors;

import ch.alpine.owl.bot.rn.RnTransition;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.RrtsNodeTransition;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.opt.nd.NdCenters;
import ch.alpine.tensor.opt.nd.NdCollectNearest;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;

public final class RnRrtsNodeCollection implements RrtsNodeCollection {
  private final NdMap<RrtsNode> ndMap;

  /** @param lbounds vector
   * @param ubounds vector */
  public RnRrtsNodeCollection(NdBox ndBox) {
    ndMap = NdTreeMap.of(ndBox); // magic const
  }

  @Override // from RrtsNodeCollection
  public void insert(RrtsNode rrtsNode) {
    ndMap.insert(rrtsNode.state(), rrtsNode);
  }

  @Override // from RrtsNodeCollection
  public int size() {
    return ndMap.size();
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNodeTransition> nearTo(Tensor tail, int k_nearest) {
    Collection<NdMatch<RrtsNode>> collection = //
        NdCollectNearest.of(ndMap, NdCenters.VECTOR_2_NORM.apply(tail), k_nearest);
    return collection.stream() //
        .map(NdMatch::value) //
        .map(rrtsNode -> new RrtsNodeTransition(rrtsNode, new RnTransition(rrtsNode.state(), tail))) //
        .collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNodeTransition> nearFrom(Tensor head, int k_nearest) {
    Collection<NdMatch<RrtsNode>> collection = //
        NdCollectNearest.of(ndMap, NdCenters.VECTOR_2_NORM.apply(head), k_nearest);
    return collection.stream() //
        .map(NdMatch::value) //
        .map(rrtsNode -> new RrtsNodeTransition(rrtsNode, new RnTransition(head, rrtsNode.state()))) //
        .collect(Collectors.toList());
  }
}
