// code by jph, gjoel
package ch.alpine.owl.rrts.adapter;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.opt.nd.NdCollectNearest;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;

/** collection of rrts nodes backed by a n-dimensional uniform tree
 * data structure is dependent on NdType */
public final class NdTypeRrtsNodeCollection implements RrtsNodeCollection {
  /** @param ndType
   * @param ndBox
   * @return */
  public static RrtsNodeCollection of(NdType ndType, NdBox ndBox) {
    return new NdTypeRrtsNodeCollection(Objects.requireNonNull(ndType), ndBox);
  }

  // ---
  private final NdType ndType;
  private final NdMap<RrtsNode> ndMap;

  private NdTypeRrtsNodeCollection(NdType ndType, NdBox ndBox) {
    this.ndType = ndType;
    ndMap = NdTreeMap.of(ndBox);
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
  public Collection<RrtsNode> nearTo(Tensor end, int k_nearest) {
    return NdCollectNearest.of(ndMap, ndType.ndCenterTo(end), k_nearest).stream() //
        .map(NdMatch::value) //
        .collect(Collectors.toList());
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    return NdCollectNearest.of(ndMap, ndType.ndCenterFrom(start), k_nearest).stream() //
        .map(NdMatch::value) //
        .collect(Collectors.toList());
  }
}
