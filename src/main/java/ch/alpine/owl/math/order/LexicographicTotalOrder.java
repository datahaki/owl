// code by astoll
package ch.alpine.owl.math.order;

import java.util.Comparator;
import java.util.List;

import ch.alpine.tensor.ext.Integers;

@SuppressWarnings({ "unchecked", "rawtypes" })
public enum LexicographicTotalOrder implements Comparator<List<Comparable>> {
  INSTANCE;

  @Override // from Comparator
  public int compare(List<Comparable> x, List<Comparable> y) {
    int size = Integers.requireEquals(x.size(), y.size());
    int cmp = 0;
    for (int index = 0; index < size && cmp == 0; ++index)
      cmp = x.get(index).compareTo(y.get(index));
    return cmp;
  }
}
