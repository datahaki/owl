// code by jph
package ch.alpine.owl.bot.r2;

import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.num.Rationalize;

/** for single integrator state space
 * use with {@link TimeIntegrators} */
public class R2RationalFlows extends R2Flows {
  private static final ScalarUnaryOperator RATIONALIZE = Rationalize.withDenominatorLessEquals(100);

  /** @param speed */
  public R2RationalFlows(Scalar speed) {
    super(speed);
  }

  @Override // from R2Flows
  protected Tensor mapU(Tensor u) {
    return u.maps(RATIONALIZE);
  }
}
