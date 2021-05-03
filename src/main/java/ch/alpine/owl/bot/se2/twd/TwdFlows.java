// code by jph
package ch.alpine.owl.bot.se2.twd;

import java.io.Serializable;

import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.N;

public abstract class TwdFlows implements FlowsInterface, Serializable {
  // ---
  private final Scalar maxSpeedHalf;
  private final Scalar halfWidth;

  /** @param maxSpeed [m*s^-1]
   * @param halfWidth [m*rad^-1] */
  public TwdFlows(Scalar maxSpeed, Scalar halfWidth) {
    maxSpeedHalf = maxSpeed.multiply(RationalScalar.HALF);
    this.halfWidth = halfWidth;
  }

  /** @param speedL in the interval [-1, 1] without unit
   * @param speedR in the interval [-1, 1] without unit
   * @return */
  protected final Tensor singleton(Scalar speedL, Scalar speedR) {
    Scalar speed = speedL.add(speedR).multiply(maxSpeedHalf);
    Scalar rate = speedR.subtract(speedL).multiply(maxSpeedHalf).divide(halfWidth);
    return N.DOUBLE.of(Tensors.of(speed, speed.zero(), rate));
  }
}
