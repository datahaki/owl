// code by jph
package ch.alpine.owl.bot.lv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;

/* package */ enum LvControls {
  ;
  public static Collection<Tensor> create(int num) {
    List<Tensor> list = new ArrayList<>();
    for (Tensor u : Subdivide.of(0.0, 1.0, num))
      list.add(Tensors.of(u));
    return list;
  }
}
