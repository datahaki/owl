// code by ynager
package ch.alpine.owl.bot.tse2;

import java.util.Collection;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Abs;

public enum Tse2Controls {
  ;
  /** @param flows
   * @return max rate per meter driven in unit "s^-1" */
  // TODO OWL API max effective turning rate/angular rate depends on tangent speed
  public static Scalar maxTurning(Collection<Tensor> flows) {
    return flows.stream() //
        .map(u -> u.Get(Tse2StateSpaceModel.CONTROL_INDEX_STEER)) //
        .map(Abs.FUNCTION) //
        .reduce(Max::of).get();
  }

  /** @param flows
   * @return min acceleration (max deceleration) with unit "m*s^-2" */
  public static Scalar minAcc(Collection<Tensor> flows) {
    return flows.stream() //
        .map(u -> u.Get(Tse2StateSpaceModel.CONTROL_INDEX_ACCEL)) //
        .reduce(Min::of).get();
  }

  /** @param flows
   * @return max acceleration with unit "m*s^-2" */
  public static Scalar maxAcc(Collection<Tensor> flows) {
    return flows.stream() //
        .map(u -> u.Get(Tse2StateSpaceModel.CONTROL_INDEX_ACCEL)) //
        .reduce(Max::of).get();
  }
}
