// code by jph
package ch.alpine.owl.bot.se2;

import java.util.Collection;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Abs;

/* package */ enum Se2Controls {
  ;
  /** @param flows
   * @return max speed with unit "m*s^-1" */
  public static Scalar maxSpeed(Collection<Tensor> flows) {
    return flows.stream().map(u -> Abs.of(u.Get(0))).reduce(Max::of).get();
  }

  /** @param flows
   * @return max rate per meter driven in unit "s^-1" */
  public static Scalar maxTurning(Collection<Tensor> flows) {
    return flows.stream().map(u -> Abs.of(u.Get(2))).reduce(Max::of).get();
  }
}
