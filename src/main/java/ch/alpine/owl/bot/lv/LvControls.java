// code by jph
package ch.alpine.owl.bot.lv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/* package */ enum LvControls {
  ;
  public static Collection<Tensor> create(Scalar u_max, int num) {
    List<Tensor> list = new ArrayList<>();
    Clip clip = Clips.positive(u_max);
    for (Tensor u : Subdivide.increasing(clip, num))
      list.add(Tensors.of(u));
    return list;
  }
}
