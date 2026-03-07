// code by jph
package ch.alpine.owl.bot.ip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/* package */ enum IpControls {
  ;
  /** @param max_force
   * @param num
   * @return */
  public static Collection<Tensor> createControls(Scalar max_force, int num) {
    Clip clip = Clips.absolute(UnitSystem.SI().apply(max_force));
    List<Tensor> list = new ArrayList<>();
    for (Tensor u : Subdivide.increasing(clip, num))
      list.add(Tensors.of(u));
    return list;
  }
}
