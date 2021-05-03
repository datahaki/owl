// code by jph
package ch.alpine.sophus.app.ubo;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Total;

/* package */ class UbongoStamp implements Serializable {
  final Tensor stamp;
  final Tensor rows;
  final Tensor cols;

  public UbongoStamp(Tensor stamp) {
    this.stamp = stamp;
    rows = Total.of(stamp);
    cols = Tensor.of(stamp.stream().map(Total::of));
  }
}
