// code by jph
package ch.alpine.sophus.ext.api;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public enum Box2D {
  ;
  public static final Tensor SQUARE = Tensors.fromString("{{0, 0}, {1, 0}, {1, 1}, {0, 1}}").unmodifiable();
  public static final Tensor CORNERS = Tensors.fromString("{{-1, -1}, {1, -1}, {1, 1}, {-1, 1}}").unmodifiable();
}
