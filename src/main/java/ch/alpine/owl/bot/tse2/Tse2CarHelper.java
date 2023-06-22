// code by jph, ynager
package ch.alpine.owl.bot.tse2;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.N;

/* package */ enum Tse2CarHelper {
  ;
  /** the turning radius of the flow is the reciprocal of the given rate
   * 
   * @param rate of turning [m^-1]
   * @param acceleration [m*s^-2]
   * @return flow with u == { rate[m^-1], acceleration[m*s^-2]} */
  public static Tensor singleton(Scalar rate, Scalar acceleration) {
    return Tensors.of(rate, acceleration).map(N.DOUBLE);
  }
}
