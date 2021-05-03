// code by gjoel
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public class RnRrtsFlow {
  public static Tensor uBetween(StateTime orig, StateTime dest) {
    Tensor direction = dest.state().subtract(orig.state());
    Scalar delta = dest.time().subtract(orig.time());
    return direction.divide(delta);
  }
}
