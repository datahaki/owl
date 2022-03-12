// code by jph
package ch.alpine.sophus.ext.api;

import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ArrayReshape;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Transpose;

public enum ImageReshape {
  ;
  public static Tensor of(Tensor wgs) {
    List<Integer> list = Dimensions.of(wgs);
    return ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), list.get(0), list.get(1) * list.get(2));
  }
}
