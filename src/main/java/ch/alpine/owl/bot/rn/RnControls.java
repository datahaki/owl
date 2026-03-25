// code by jph
package ch.alpine.owl.bot.rn;

import java.util.Collection;

import ch.alpine.sophis.flow.StateSpaceModels;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.red.Max;

/** utility functions for controls in R^n in combination with
 * {@link StateSpaceModels}
 * 
 * class is intentionally public */
enum RnControls {
  ;
  /** @param controls
   * @return max of norm 2 of given controls in R^n */
  public static Scalar maxSpeed(Collection<Tensor> controls) {
    return controls.stream() //
        .map(Vector2Norm::of) //
        .reduce(Max::of) //
        .orElseThrow();
  }
}
