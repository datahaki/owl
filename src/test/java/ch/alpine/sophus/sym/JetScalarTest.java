// code by jph
package ch.alpine.sophus.sym;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Exp;
import ch.alpine.tensor.sca.Log;
import ch.alpine.tensor.sca.Power;
import ch.alpine.tensor.sca.Sin;
import ch.alpine.tensor.sca.Sinh;
import ch.alpine.tensor.sca.Sqrt;
import junit.framework.TestCase;

public class JetScalarTest extends TestCase {
  public void testMultiply() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2));
    Scalar s2 = JetScalar.of(Tensors.vector(2, 3, -1));
    Scalar scalar = s1.multiply(s2);
    JetScalar jetScalar = (JetScalar) scalar;
    assertEquals(jetScalar.vector(), Tensors.vector(8, 14, 6));
  }

  public void testReciprocal() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2));
    Scalar reciprocal = s1.reciprocal();
    assertEquals(((JetScalar) reciprocal).vector(), Tensors.fromString("{1/4, -1/16, -3/32}"));
    Scalar neutral = s1.multiply(reciprocal);
    assertEquals(((JetScalar) neutral).vector(), UnitVector.of(3, 0));
  }

  public void testPower() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2, -3));
    Scalar scalar = Power.of(s1, 5);
    JetScalar jetScalar = (JetScalar) scalar;
    assertEquals(jetScalar.vector(), Tensors.vector(1024, 1280, 3840, 4800));
  }

  public void testScalar() {
    Scalar s1 = JetScalar.of(RealScalar.of(3), 4);
    JetScalar jetScalar = (JetScalar) s1;
    assertEquals(jetScalar.vector(), Tensors.vector(3, 1, 0, 0));
  }

  public void testNegate() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2, -3));
    Scalar s2 = RealScalar.of(3);
    JetScalar jetScalar = (JetScalar) s2.multiply(s1);
    assertEquals(jetScalar.vector(), Tensors.vector(12, 3, 6, -9));
  }

  public void testSqrt() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 1, -3));
    JetScalar scalar = (JetScalar) Sqrt.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), Tensors.vector(2, 0.5, 0.125, -0.84375));
  }

  public void testExp() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Exp.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), //
        Tensors.vector(54.598150033144236, 109.19630006628847, 218.39260013257694, 272.9907501657212));
  }

  public void testLog() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Log.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), Tensors.vector(1.3862943611198906, 0.5, -0.25, -0.5));
  }

  public void testSin() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Sin.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), //
        Tensors.vector(-0.7568024953079282, -1.3072872417272239, 3.027209981231713, 7.190079829499732));
  }

  public void testSinh() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Sinh.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), //
        Tensors.vector(27.28991719712775, 54.61646567203297, 109.159668788511, 136.54116418008243));
  }

  public void testPolynomial() {
    Tensor coeffs = Tensors.vector(2, 1, 3, 4);
    ScalarUnaryOperator f0 = Polynomial.of(coeffs);
    ScalarUnaryOperator f1 = Polynomial.derivative(coeffs);
    Scalar value = RationalScalar.of(3, 17);
    Tensor gnd = Tensors.of(f0.apply(value), f1.apply(value));
    Scalar scalar = JetScalar.of(value, gnd.length());
    JetScalar der = (JetScalar) f0.apply(scalar);
    assertEquals(der.vector(), gnd);
  }

  public void testPolynomialRandom() {
    Tensor c0 = RandomVariate.of(DiscreteUniformDistribution.of(-3, 3), 4);
    ScalarUnaryOperator f0 = Polynomial.of(c0);
    Tensor c1 = Polynomial.derivative_coeffs(c0);
    ScalarUnaryOperator f1 = Polynomial.of(c1);
    Tensor c2 = Polynomial.derivative_coeffs(c1);
    ScalarUnaryOperator f2 = Polynomial.of(c2);
    Scalar value = RationalScalar.of(3, 17);
    Tensor gnd = Tensors.of(f0.apply(value), f1.apply(value), f2.apply(value));
    Scalar scalar = JetScalar.of(value, gnd.length());
    JetScalar der = (JetScalar) f0.apply(scalar);
    assertEquals(der.vector(), gnd);
  }

  public void testScalarFail() {
    AssertFail.of(() -> JetScalar.of(RealScalar.of(2)));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> JetScalar.of(HilbertMatrix.of(3)));
  }
}
