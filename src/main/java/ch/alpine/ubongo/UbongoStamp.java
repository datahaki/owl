// code by jph
package ch.alpine.ubongo;

import java.io.Serializable;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.red.Total;

/* package */ class UbongoStamp implements Serializable {
  final Tensor stamp;
  final Tensor rows;
  final Tensor cols;
  final int size0;
  final int size1;

  public UbongoStamp(Tensor stamp) {
    this.stamp = stamp;
    List<Integer> list = Dimensions.of(stamp);
    size0 = list.get(0);
    size1 = list.get(1);
    rows = Total.of(stamp);
    cols = Tensor.of(stamp.stream().map(Total::of));
  }
}
