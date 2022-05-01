// code by jph
package ch.alpine.owl.bot.rn;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class RnControlsTest {
  @Test
  public void testMaxSpeed() {
    int n = 10;
    R2Flows r2Flows = new R2Flows(Quantity.of(3, "m*s^-1"));
    Collection<Tensor> controls = r2Flows.getFlows(n);
    Scalar maxSpeed = RnControls.maxSpeed(controls);
    Chop._14.requireClose(maxSpeed, Quantity.of(3, "m*s^-1"));
  }
}
