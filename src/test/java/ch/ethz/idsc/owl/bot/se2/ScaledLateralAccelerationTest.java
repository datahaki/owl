// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.owl.bot.se2.twd.TwdDuckieFlows;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.qty.Unit;
import junit.framework.TestCase;

public class ScaledLateralAccelerationTest extends TestCase {
  public void testCar() {
    final Scalar ms = Quantity.of(2, "m*s^-1");
    final Scalar mr = Scalars.fromString("3[rad*m^-1]");
    Flow flow = CarHelper.singleton(ms, mr);
    assertEquals(QuantityUnit.of(flow.getU().Get(2)), Unit.of("rad*s^-1"));
    Tensor u = flow.getU();
    ScaledLateralAcceleration se2LateralAcceleration = new ScaledLateralAcceleration(Quantity.of(1, "CHF*s*rad^-2"));
    Scalar cost = se2LateralAcceleration.cost(u, Quantity.of(3, "s"));
    assertEquals(QuantityUnit.of(cost), Unit.of("CHF"));
  }

  public void testTwd() {
    Scalar ms = Quantity.of(3, "m*s^-1");
    Scalar sa = Quantity.of(0.567, "m*rad^-1");
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(ms, sa);
    Collection<Flow> controls = twdConfig.getFlows(8);
    Tensor u = controls.iterator().next().getU();
    ScaledLateralAcceleration se2LateralAcceleration = new ScaledLateralAcceleration(RealScalar.ONE);
    Scalar cost = se2LateralAcceleration.cost(u, Quantity.of(3, "s"));
    assertEquals(QuantityUnit.of(cost), Unit.of("rad^2*s^-1"));
  }

  public void testTwdDouble() {
    Scalar ms = Quantity.of(3, "m*s^-1");
    Scalar sa = Quantity.of(0.567, "m*rad^-1");
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(ms, sa);
    Collection<Flow> controls = twdConfig.getFlows(8);
    Tensor u = controls.iterator().next().getU();
    ScaledLateralAcceleration se2LateralAcceleration = new ScaledLateralAcceleration(Quantity.of(1 / 83.98421096833796, "s*rad^-2"));
    Scalar cost = se2LateralAcceleration.cost(u, Quantity.of(3, "s"));
    assertEquals(QuantityUnit.of(cost), Unit.ONE);
    assertEquals(cost, RealScalar.ONE);
  }
}
