// code by jph
package ch.alpine.owl.math;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Floor;

public class LinearRasterMap<T> extends RasterMap<T> {
  private final Tensor scale;

  public LinearRasterMap(Tensor scale) {
    this.scale = scale;
  }

  @Override
  public Tensor toKey(Tensor tensor) {
    return Floor.of(Times.of(tensor, scale));
  }
}
