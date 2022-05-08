// code by jph
package ch.alpine.owl.bot.se2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.glc.Se2CarFlows;
import ch.alpine.owl.bot.se2.twd.TwdDuckieFlows;
import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;

class Se2ControlsTest {
  @Test
  public void testSimple() {
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, Degree.of(45));
    Collection<Tensor> controls = carFlows.getFlows(6);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    Chop._13.requireClose(maxSpeed, RealScalar.ONE);
    Scalar maxTurn = Se2Controls.maxTurning(controls);
    Chop._13.requireClose(maxTurn, RealScalar.of(45 * Math.PI / 180));
    Chop._13.requireClose(maxTurn, Degree.of(45));
  }

  @Test
  public void testMaxRate() {
    List<Tensor> list = new ArrayList<>();
    for (Tensor angle : Subdivide.of(RealScalar.of(-.1), RealScalar.of(0.3), 5))
      list.add(Se2CarFlows.singleton(RealScalar.of(2), (Scalar) angle));
    Scalar maxR = Se2Controls.maxTurning(list);
    assertEquals(maxR, RealScalar.of(0.6));
  }

  @Test
  public void testMaxRate2() {
    List<Tensor> list = new ArrayList<>();
    for (Tensor angle : Subdivide.of(RealScalar.of(-.3), RealScalar.of(0.1), 5))
      list.add(Se2CarFlows.singleton(RealScalar.of(2), (Scalar) angle));
    Scalar maxR = Se2Controls.maxTurning(list);
    assertEquals(maxR, RealScalar.of(0.6));
  }

  @Test
  public void testUnits() {
    final Scalar ms = Quantity.of(2, "m*s^-1");
    final Scalar mr = Scalars.fromString("3[m^-1]");
    Tensor flow = Se2CarFlows.singleton(ms, mr);
    assertEquals(QuantityUnit.of(flow.Get(2)), Unit.of("s^-1"));
    Collection<Tensor> controls = Collections.singleton(flow);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertEquals(maxSpeed, Abs.FUNCTION.apply(ms));
    assertEquals(QuantityUnit.of(maxSpeed), Unit.of("m*s^-1"));
    Scalar maxTurning = Se2Controls.maxTurning(controls);
    assertEquals(QuantityUnit.of(maxTurning), Unit.of("s^-1"));
    assertEquals(maxTurning, Quantity.of(6, "s^-1"));
  }

  @Test
  public void testUnitsNonSI() {
    final Scalar ms = Quantity.of(2, "m*s^-1");
    final Scalar mr = Scalars.fromString("3[rad*m^-1]");
    Tensor flow = Se2CarFlows.singleton(ms, mr);
    assertEquals(QuantityUnit.of(flow.Get(2)), Unit.of("rad*s^-1"));
    Collection<Tensor> controls = Collections.singleton(flow);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertEquals(maxSpeed, Abs.FUNCTION.apply(ms));
    assertEquals(QuantityUnit.of(maxSpeed), Unit.of("m*s^-1"));
    Scalar maxTurning = Se2Controls.maxTurning(controls);
    assertEquals(QuantityUnit.of(maxTurning), Unit.of("rad*s^-1"));
    assertEquals(maxTurning, Quantity.of(6, "rad*s^-1"));
  }

  @Test
  public void testMaxSpeed() {
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(RealScalar.of(3), RealScalar.of(0.567));
    Collection<Tensor> controls = twdConfig.getFlows(8);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertEquals(maxSpeed, RealScalar.of(3));
  }

  @Test
  public void testUnit() {
    Scalar ms = Quantity.of(3, "m*s^-1");
    Scalar sa = Quantity.of(0.567, "m*rad^-1");
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(ms, sa);
    Collection<Tensor> controls = twdConfig.getFlows(8);
    Scalar maxSpeed = Se2Controls.maxSpeed(controls);
    assertEquals(maxSpeed, ms);
    assertEquals(QuantityUnit.of(maxSpeed), Unit.of("m*s^-1"));
    Scalar maxTurng = Se2Controls.maxTurning(controls);
    assertEquals(QuantityUnit.of(maxTurng), Unit.of("rad*s^-1"));
    assertEquals(maxTurng, ms.divide(sa));
  }
}
