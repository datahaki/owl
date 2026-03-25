// code by jph
package ch.alpine.owl.bot.psu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** Pendulum Swing-up controls */
enum PsuControls {
  ;
  /** @param amplitude maximum absolute radial acceleration of pendulum
   * @param num
   * @return */
  public static Collection<Tensor> createControls(Scalar amplitude, int num) {
    List<Tensor> list = new ArrayList<>();
    Clip clip = Clips.absolute(amplitude);
    for (Tensor u : Subdivide.increasing(clip, num))
      list.add(Tensors.of(u));
    return list;
  }
}
