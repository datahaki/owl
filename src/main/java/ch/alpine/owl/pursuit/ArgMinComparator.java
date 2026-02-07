// code by gjoel
package ch.alpine.owl.pursuit;

import java.util.Comparator;
import java.util.Objects;

import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;

/* package */ enum ArgMinComparator implements Comparator<Tensor> {
  INSTANCE;

  @Override // from Comparator
  public int compare(Tensor t1, Tensor t2) {
    if (Objects.isNull(t1))
      return 1;
    if (Objects.isNull(t2))
      return -1;
    return Scalars.compare(t1.Get(0), t2.Get(0));
  }
}
