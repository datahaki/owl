// code by jph, gjoel
package ch.alpine.owl.rrts.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.alpine.java.util.RandomElements;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.tensor.Tensor;

/** node collection that implements the nearest-neighbor query to return a random subset */
public class RandomRrtsNodeCollection implements RrtsNodeCollection {
  private final List<RrtsNode> list = new ArrayList<>();

  @Override // from RrtsNodeCollection
  public void insert(RrtsNode rrtsNode) {
    list.add(rrtsNode);
  }

  @Override // from RrtsNodeCollection
  public int size() {
    return list.size();
  }

  @Override // from RrtsNodeCollection
  public Collection<RrtsNode> nearFrom(Tensor start, int k_nearest) {
    return nearTo(start, k_nearest);
  }

  @Override // from RrtsNodeCollection
  public synchronized Collection<RrtsNode> nearTo(Tensor end, int k_nearest) {
    return RandomElements.of(list, k_nearest);
  }
}
