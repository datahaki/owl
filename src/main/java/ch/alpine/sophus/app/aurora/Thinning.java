// code by jph
package ch.alpine.sophus.app.aurora;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ enum Thinning {
  ;
  public static Tensor of(Tensor tensor, int delta) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < tensor.length(); index += delta)
      result.append(tensor.get(index));
    return result;
  }
}
