// code by jph
package ch.alpine.owl.bot.ip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.alg.Subdivide;

/* package */ enum IpControls {
  ;
  /** @param stateSpaceModel
   * @param amplitude maximum absolute radial acceleration of pendulum
   * @param num
   * @return */
  public static Collection<Tensor> createControls(double amplitude, int num) {
    List<Tensor> list = new ArrayList<>();
    for (Tensor u : Partition.of(Subdivide.of(-amplitude, amplitude, num), 1))
      list.add(u);
    return list;
  }
}
