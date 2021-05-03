// code by jph
package ch.alpine.owl.bot.delta;

import java.util.Collection;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.red.Max;

/* package */ enum DeltaControls {
  ;
  /** @param controls
   * @return */
  public static Scalar maxSpeed(Collection<Tensor> controls) {
    return controls.stream().map(Vector2Norm::of).reduce(Max::of).get();
  }
}
