// code by gjoel
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.bot.se2.Se2Wrap;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.d2.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.nrm.Vector2Norm;

public enum CarRrtsFlow {
  ;
  /** @param orig
   * @param dest
   * @return */
  public static Tensor uBetween(StateTime orig, StateTime dest) {
    Tensor log = Se2Wrap.INSTANCE.difference(orig.state(), dest.state());
    Scalar delta = dest.time().subtract(orig.time());
    // TODO side speed should not result in forward motion! rather project
    // ... the sign of vx is not always correct when using norm!
    Scalar vx = Vector2Norm.of(Extract2D.FUNCTION.apply(log));
    return Tensors.of(vx, vx.zero(), log.Get(2)).divide(delta);
  }
}
