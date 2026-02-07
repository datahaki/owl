// code by jph
package ch.alpine.sophus.math.bij;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.se2.Se2AxisYProject;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.sophus.math.sample.BallRandomSample;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Chop;

class Se2AxisYProjectTest {
  @Test
  void testEx1() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(1, 0, 0.3)).apply(Tensors.vector(10, 0));
    Chop._12.requireClose(t, RealScalar.of(4.163485907994182));
  }

  @Test
  void testEx2() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(1, 0, 0.3)).apply(Tensors.vector(10, 3));
    Chop._12.requireClose(t, RealScalar.of(5.124917769722165));
  }

  @Test
  void testEx2NegU() {
    double speed = -1;
    Tensor u = Tensors.vector(1 * speed, 0, 0.3 * speed);
    Tensor p = Tensors.vector(-10, 3);
    Scalar t = Se2AxisYProject.of(u).apply(p);
    Chop._12.requireClose(t, RealScalar.of(5.124917769722165));
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringGroup.INSTANCE.exponential0().exp(u.multiply(t.negate()))).forward();
    Tensor v = se2ForwardAction.apply(p);
    Chop._13.requireClose(v, Tensors.fromString("{0, -6.672220679869088}"));
  }

  @Test
  void testEx2Neg() {
    Tensor u = Tensors.vector(1, 0, 0.3);
    Tensor p = Tensors.vector(-10, 3);
    Scalar t = Se2AxisYProject.of(u).apply(p);
    Chop._12.requireClose(t, RealScalar.of(-5.124917769722165));
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringGroup.INSTANCE.exponential0().exp(u.multiply(t.negate()))).forward();
    Tensor v = se2ForwardAction.apply(p);
    Chop._13.requireClose(v, Tensors.fromString("{0, -6.672220679869088}"));
  }

  @Test
  void testEx3() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(1, 0, 0.0)).apply(Tensors.vector(10, 3));
    Chop._12.requireClose(t, RealScalar.of(10));
  }

  @Test
  void testEx4() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(2, 0, 0.0)).apply(Tensors.vector(10, 3));
    Chop._12.requireClose(t, RealScalar.of(5));
  }

  @Test
  void testEx4NegU() {
    Scalar t = Se2AxisYProject.of(Tensors.vector(-2, 0, 0.0)).apply(Tensors.vector(10, 3));
    Chop._12.requireClose(t, RealScalar.of(-5));
  }

  @Test
  void testEx4Neg() {
    Tensor u = Tensors.vector(2, 0, 0);
    Tensor p = Tensors.vector(-10, 3);
    Scalar t = Se2AxisYProject.of(u).apply(p);
    Chop._12.requireClose(t, RealScalar.of(-5));
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringGroup.INSTANCE.exponential0().exp(u.multiply(t.negate()))).forward();
    Tensor v = se2ForwardAction.apply(p);
    assertEquals(v, Tensors.vector(0, 3));
  }

  @Test
  void testEps1() {
    Tensor u = Tensors.vector(2, 0, Double.MIN_VALUE);
    Scalar t = Se2AxisYProject.of(u).apply(Tensors.vector(10, 3));
    assertFalse(Scalars.isZero(u.Get(2)));
    Chop._12.requireClose(t, RealScalar.of(5));
  }

  @Test
  void testEps2() {
    Tensor u = Tensors.vector(2, 0, -Double.MIN_VALUE);
    Scalar t = Se2AxisYProject.of(u).apply(Tensors.vector(10, 3));
    assertFalse(Scalars.isZero(u.Get(2)));
    Chop._12.requireClose(t, RealScalar.of(5));
  }

  @Test
  void testZeroSpeedNonZeroPos() {
    Tensor u = Tensors.vector(0, 0, 0);
    Scalar t = Se2AxisYProject.of(u).apply(Tensors.vector(10, 3));
    assertEquals(t, DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testZeroSpeedNonZeroPos2() {
    Tensor u = Tensors.vector(0, 0, 0);
    Scalar t = Se2AxisYProject.of(u).apply(Tensors.vector(-10, 3));
    assertEquals(t, DoubleScalar.POSITIVE_INFINITY.negate());
  }

  @Test
  void testZeroSpeedZeroPos() {
    Tensor u = Tensors.vector(0, 0, 0);
    Scalar t = Se2AxisYProject.of(u).apply(Tensors.vector(0, 3));
    assertEquals(t, RealScalar.ZERO);
  }

  @Test
  void testCheck() {
    RandomSampleInterface rsi = BallRandomSample.of(Tensors.vector(0, 0), RealScalar.of(10));
    for (int index = 0; index < 100; ++index) {
      Tensor u = Tensors.vector(0.9, 0, 0.3);
      Tensor p = RandomSample.of(rsi);
      Scalar t = Se2AxisYProject.of(u).apply(p).negate();
      Tensor m = Se2Matrix.of(Se2CoveringGroup.INSTANCE.exponential0().exp(u.multiply(t)));
      Tensor v = m.dot(Append.of(p, RealScalar.ONE));
      Chop._12.requireAllZero(v.Get(0));
    }
  }

  @Test
  void testCheck2() {
    RandomSampleInterface rsi = BallRandomSample.of(Tensors.vector(0, 0), RealScalar.of(10));
    for (int index = 0; index < 100; ++index) {
      Tensor u = Tensors.vector(1.1, 0, 1.3);
      Tensor p = RandomSample.of(rsi);
      Scalar t = Se2AxisYProject.of(u).apply(p).negate();
      Tensor m = Se2Matrix.of(Se2CoveringGroup.INSTANCE.exponential0().exp(u.multiply(t)));
      Tensor v = m.dot(p.copy().append(RealScalar.ONE));
      Chop._12.requireAllZero(v.Get(0));
    }
  }

  @Test
  void testCheckZero() {
    RandomSampleInterface rsi = BallRandomSample.of(Tensors.vector(0, 0), RealScalar.of(10));
    for (int index = 0; index < 100; ++index) {
      Tensor u = Tensors.vector(2, 0, 0);
      Tensor p = RandomSample.of(rsi);
      Scalar t = Se2AxisYProject.of(u).apply(p).negate();
      Tensor m = Se2Matrix.of(Se2CoveringGroup.INSTANCE.exponential0().exp(u.multiply(t)));
      Tensor v = m.dot(p.copy().append(RealScalar.ONE));
      Chop._12.requireAllZero(v.Get(0));
    }
  }

  @Test
  void testUnitsNormal() {
    // [m*s^-1], 0.0, (rate*speed)[rad*s^-1]
    Tensor u = Tensors.fromString("{1.1[m*s^-1], 0, 1.3[s^-1]}"); // SI
    Tensor p = Tensors.fromString("{2.1[m], 0.7[m]}");
    Scalar t = Se2AxisYProject.of(u).apply(p);
    Scalar magnitude = QuantityMagnitude.SI().in("s").apply(t);
    Chop._10.requireClose(magnitude, DoubleScalar.of(1.154854847741819));
    assertEquals(QuantityUnit.of(t), Unit.of("s"));
    assertTrue(Scalars.nonZero(t));
  }

  @Test
  void testUnitsBeZero() {
    // [m*s^-1], 0.0, (rate*speed)[rad*s^-1]
    Tensor u = Tensors.fromString("{1.1[m*s^-1], 0, 0[s^-1]}"); // SI
    Tensor p = Tensors.fromString("{2.1[m], 0.7[m]}");
    Scalar t = Se2AxisYProject.of(u).apply(p);
    assertEquals(QuantityUnit.of(t), Unit.of("s"));
    assertTrue(Scalars.nonZero(t));
  }

  @Test
  void testUnitsBeZeroVxZero1() {
    // [m*s^-1], 0.0, (rate*speed)[rad*s^-1]
    Tensor u = Tensors.fromString("{0.0[m*s^-1], 0, 0[s^-1]}"); // SI
    Tensor p = Tensors.fromString("{2.1[m], 0.7[m]}");
    Scalar t = Se2AxisYProject.of(u).apply(p);
    assertEquals(QuantityUnit.of(t), Unit.of("s"));
    assertTrue(Scalars.nonZero(t));
  }

  @Test
  void testUnitsBeZeroVxZero2() {
    // [m*s^-1], 0.0, (rate*speed)[rad*s^-1]
    Tensor u = Tensors.fromString("{0.0[m*s^-1], 0, 0[s^-1]}"); // SI
    Tensor p = Tensors.fromString("{0.0[m], 0.7[m]}");
    Scalar t = Se2AxisYProject.of(u).apply(p);
    assertEquals(QuantityUnit.of(t), Unit.of("s"));
    assertTrue(Scalars.isZero(t));
  }
}
