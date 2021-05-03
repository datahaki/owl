// code by jph
package ch.alpine.owl.bot.se2;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Cos;
import junit.framework.TestCase;

public class DifferentialSpeedTest extends TestCase {
  public void testSimple() {
    DifferentialSpeed ds = DifferentialSpeed.fromSI(RealScalar.of(1.2), RealScalar.of(0.5));
    Scalar speed = RealScalar.of(+4.0);
    Scalar angle = RealScalar.of(+0.3);
    // confirmed with mathematica
    Chop._10.requireClose(ds.get(speed.divide(Cos.FUNCTION.apply(angle)), angle), RealScalar.of(3.4844395839839613));
    assertEquals(ds.get(speed, RealScalar.ZERO), speed);
    Chop._10.requireClose(ds.get(speed.divide(Cos.FUNCTION.apply(angle)), angle.negate()), RealScalar.of(4.515560416016039));
  }

  public void testQuantityForward() {
    Scalar y_offset = Quantity.of(0.5, "m");
    DifferentialSpeed tireRearL = DifferentialSpeed.fromSI(Quantity.of(1.2, "m"), y_offset);
    DifferentialSpeed tireRearR = DifferentialSpeed.fromSI(Quantity.of(1.2, "m"), y_offset.negate());
    Scalar speed = Quantity.of(+4.0, "m*s^-1");
    {
      Scalar angle = RealScalar.of(+0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireL, tireR));
    }
    {
      Scalar angle = RealScalar.of(-0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireR, tireL));
    }
  }

  public void testQuantityPair() {
    Scalar y_offset = Quantity.of(0.5, "m");
    DifferentialSpeed tireRearL = DifferentialSpeed.fromSI(Quantity.of(1.2, "m"), y_offset);
    DifferentialSpeed tireRearR = DifferentialSpeed.fromSI(Quantity.of(1.2, "m"), y_offset.negate());
    Scalar speed = Quantity.of(+4.0, "m*s^-1");
    {
      Scalar angle = RealScalar.of(+0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireL, tireR));
      Tensor pair = tireRearL.pair(speed, angle);
      assertEquals(pair, Tensors.of(tireL, tireR));
    }
    {
      Scalar angle = RealScalar.of(-0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireR, tireL));
      Tensor pair = tireRearL.pair(speed, angle);
      assertEquals(pair, Tensors.of(tireL, tireR));
    }
  }

  public void testQuantityPairRadians() {
    Scalar y_offset = Quantity.of(0.5, "m");
    DifferentialSpeed tireRearL = DifferentialSpeed.fromSI(Quantity.of(1.2, "m"), y_offset);
    DifferentialSpeed tireRearR = DifferentialSpeed.fromSI(Quantity.of(1.2, "m"), y_offset.negate());
    Scalar speed = Quantity.of(+4.0, "s^-1");
    {
      Scalar angle = RealScalar.of(+0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireL, tireR));
      Tensor pair = tireRearL.pair(speed, angle);
      assertEquals(pair, Tensors.of(tireL, tireR));
      assertEquals(QuantityUnit.of(pair.Get(0)), Unit.of("s^-1"));
      assertEquals(QuantityUnit.of(pair.Get(1)), Unit.of("s^-1"));
    }
    {
      Scalar angle = RealScalar.of(-0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireR, tireL));
      Tensor pair = tireRearL.pair(speed, angle);
      assertEquals(pair, Tensors.of(tireL, tireR));
    }
  }

  public void testQuantityReverse() {
    Scalar y_offset = Quantity.of(0.5, "m");
    DifferentialSpeed tireRearL = DifferentialSpeed.fromSI(Quantity.of(1.2, "m"), y_offset);
    DifferentialSpeed tireRearR = DifferentialSpeed.fromSI(Quantity.of(1.2, "m"), y_offset.negate());
    Scalar speed = Quantity.of(-4.0, "m*s^-1");
    {
      Scalar angle = RealScalar.of(+0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireR, tireL));
    }
    {
      Scalar angle = RealScalar.of(-0.3);
      Scalar tireL = tireRearL.get(speed, angle);
      Scalar tireR = tireRearR.get(speed, angle);
      assertTrue(Scalars.lessThan(tireL, tireR));
    }
  }

  public void testStraight() {
    DifferentialSpeed dsL = DifferentialSpeed.fromSI(RealScalar.of(1.2), RealScalar.of(+.5));
    DifferentialSpeed dsR = DifferentialSpeed.fromSI(RealScalar.of(1.2), RealScalar.of(-.5));
    Scalar v = RealScalar.of(-4);
    Scalar beta = RealScalar.ZERO;
    Scalar rL = dsL.get(v, beta);
    Scalar rR = dsR.get(v, beta);
    assertEquals(rL, v);
    assertEquals(rR, v);
  }

  public void testOrthogonal() {
    DifferentialSpeed dsL = DifferentialSpeed.fromSI(RealScalar.of(1.2), RealScalar.of(+.5));
    DifferentialSpeed dsR = DifferentialSpeed.fromSI(RealScalar.of(1.2), RealScalar.of(-.5));
    Scalar v = RealScalar.of(4);
    Scalar beta = Pi.HALF;
    Scalar rL = dsL.get(v, beta);
    Scalar rR = dsR.get(v, beta);
    Chop._12.requireClose(rL, rR.negate());
  }

  public void testInverted() {
    DifferentialSpeed ds = DifferentialSpeed.fromSI(RealScalar.of(1.2), RealScalar.of(-.5));
    Scalar v = RealScalar.of(4);
    Scalar beta = RealScalar.of(+.3);
    // confirmed with mathematica
    Chop._10.requireClose(ds.get(v.divide(Cos.FUNCTION.apply(beta)), beta), RealScalar.of(4.515560416016039));
    assertEquals(ds.get(v, RealScalar.ZERO), v);
    Chop._10.requireClose(ds.get(v.divide(Cos.FUNCTION.apply(beta)), beta.negate()), RealScalar.of(3.4844395839839613));
  }

  public void testFail() {
    AssertFail.of(() -> DifferentialSpeed.fromSI(RealScalar.of(0.0), RealScalar.of(0.5)));
  }
}
