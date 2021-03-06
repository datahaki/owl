// code by jph
package ch.alpine.sophus.app.sym;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Inverse;
import ch.alpine.tensor.mat.LinearSolve;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class RootScalarTest extends TestCase {
  public void testSimple() {
    RootScalar r1 = new RootScalar(RealScalar.of(-7), RealScalar.of(3), RealScalar.of(2));
    RootScalar r2 = new RootScalar(RealScalar.of(+8), RealScalar.of(6), RealScalar.of(2));
    assertEquals(r1.add(r2), new RootScalar(RealScalar.of(1), RealScalar.of(9), RealScalar.of(2)));
    assertEquals(r1.multiply(r2), new RootScalar(RealScalar.of(-20), RealScalar.of(-18), RealScalar.of(2)));
    assertEquals(r1.subtract(r1), RealScalar.ZERO);
  }

  public void testReciprocal() {
    RootScalar rootScalar = new RootScalar(RationalScalar.of(-7, 13), RationalScalar.of(3, -11), RealScalar.of(2));
    assertEquals(rootScalar.multiply(rootScalar.reciprocal()), RealScalar.ONE);
  }

  public void testNumber() {
    RootScalar rootScalar = new RootScalar(RationalScalar.of(-7, 5), RationalScalar.of(-3, 17), RealScalar.of(3));
    Number number = rootScalar.number();
    double value = -7 / 5.0 - 3 / 17.0 * Math.sqrt(3);
    Chop._10.requireClose(RealScalar.of(number), RealScalar.of(value));
  }

  public void testZero() {
    RootScalar rootScalar = new RootScalar(RationalScalar.of(-2, 13), RationalScalar.of(-8, 11), RealScalar.of(2));
    Scalar zero = rootScalar.zero();
    assertEquals(zero, RealScalar.ZERO);
  }

  public void testEquations() {
    Distribution distribution = DiscreteUniformDistribution.of(-3000, 3000);
    Scalar ba = RealScalar.of(2);
    Tensor matrix = //
        Tensors.matrix((i, j) -> new RootScalar(RandomVariate.of(distribution), RandomVariate.of(distribution), ba), 7, 7);
    Tensor rhs = //
        Tensors.matrix((i, j) -> new RootScalar(RandomVariate.of(distribution), RandomVariate.of(distribution), ba), 7, 3);
    Tensor sol = LinearSolve.of(matrix, rhs);
    assertEquals(matrix.dot(sol), rhs);
  }

  public void testInverse() {
    Distribution distribution = DiscreteUniformDistribution.of(-3000, 3000);
    Scalar ba = RealScalar.of(2);
    Tensor matrix = //
        Tensors.matrix((i, j) -> new RootScalar(RandomVariate.of(distribution), RandomVariate.of(distribution), ba), 5, 5);
    Tensor inv = Inverse.of(matrix);
    assertEquals(matrix.dot(inv), IdentityMatrix.of(5));
  }
}
