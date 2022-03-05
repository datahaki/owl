// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.tri.ArcTan;
import junit.framework.TestCase;

public class ConeRegionTest extends TestCase {
  public void testSimple() {
    ConeRegion coneRegion = new ConeRegion(Tensors.vector(5, 0, Math.PI / 2), RealScalar.ONE);
    assertTrue(coneRegion.test(Tensors.vector(6, 2)));
    assertTrue(coneRegion.test(Tensors.vector(4, 2)));
    assertFalse(coneRegion.test(Tensors.vector(0, 2)));
    assertFalse(coneRegion.test(Tensors.vector(6, -2)));
  }

  public void test90deg() {
    ConeRegion coneRegion = new ConeRegion(Tensors.vector(5, 0, Math.PI / 2), RealScalar.of(Math.PI / 4));
    assertTrue(coneRegion.test(Tensors.vector(5 + 2, 2.1)));
    assertFalse(coneRegion.test(Tensors.vector(5 + 2, 1.9)));
  }

  public void testUnits() {
    ConeRegion coneRegion = new ConeRegion(Tensors.fromString("{3[m], 4[m], 1.5}"), RealScalar.of(1));
    assertTrue(coneRegion.test(Tensors.fromString("{3[m], 4+1[m]}")));
    assertFalse(coneRegion.test(Tensors.fromString("{3[m], 4-1[m]}")));
  }

  public void testDistance() {
    ConeRegion coneRegion = new ConeRegion(Array.zeros(3), RealScalar.of(Math.PI / 4));
    assertEquals(coneRegion.distance(Tensors.vector(1, .1)), RealScalar.ZERO);
    assertEquals(coneRegion.distance(Tensors.vector(-4, 3)), RealScalar.of(5));
    assertEquals(coneRegion.distance(Tensors.vector(-4, -3)), RealScalar.of(5));
    Chop._06.requireClose(coneRegion.distance(Tensors.vector(-1, 1)), RealScalar.of(Math.sqrt(2)));
    Chop._06.requireClose(coneRegion.distance(Tensors.vector(-1, -1)), RealScalar.of(Math.sqrt(2)));
  }

  public void testDistanceRandom() {
    ConeRegion coneRegion = new ConeRegion(Tensors.vector(0.3, -0.2, -0.1), RealScalar.of(Math.PI / 4));
    for (Tensor tensor : RandomVariate.of(NormalDistribution.standard(), 100, 2))
      assertEquals(coneRegion.test(tensor), Scalars.isZero(coneRegion.distance(tensor)));
  }

  public void testObtuseRandom() {
    ConeRegion coneRegion = new ConeRegion(Tensors.vector(0.1, 0.2, 0.3), RealScalar.of(Math.PI * 0.9));
    for (Tensor tensor : RandomVariate.of(NormalDistribution.standard(), 100, 2))
      assertEquals(coneRegion.test(tensor), Scalars.isZero(coneRegion.distance(tensor)));
    assertEquals(coneRegion.apex(), Tensors.vector(0.1, 0.2, 0.3));
    assertEquals(coneRegion.semi(), RealScalar.of(Math.PI * 0.9));
  }

  public void testCompleteRandom() {
    ConeRegion coneRegion = new ConeRegion(Tensors.vector(0.1, -0.1, 0.3), RealScalar.of(Math.PI * 1.1));
    for (Tensor tensor : RandomVariate.of(NormalDistribution.standard(), 100, 2)) {
      assertTrue(coneRegion.test(tensor));
      assertEquals(coneRegion.test(tensor), Scalars.isZero(coneRegion.distance(tensor)));
    }
    assertEquals(coneRegion.apex(), Tensors.vector(0.1, -0.1, 0.3));
    assertEquals(coneRegion.semi(), RealScalar.of(Math.PI * 1.1));
  }

  public void testNegativeBug() {
    for (Tensor _radius : Subdivide.of(0.1, 3 * Math.PI, 10)) {
      ConeRegion coneRegion = new ConeRegion(Tensors.vector(0, 0, 6 * Math.PI), (Scalar) _radius);
      for (Tensor tensor : RandomVariate.of(NormalDistribution.standard(), 50, 2))
        Sign.requirePositiveOrZero(coneRegion.distance(tensor));
    }
  }

  public void testArcTan() {
    Sign.requirePositive(ArcTan.of(RealScalar.of(-3), RealScalar.ZERO));
  }

  public void testObtuseAngle() {
    ConeRegion coneRegion = new ConeRegion(Array.zeros(3), RealScalar.of(Math.PI * 0.99));
    Tensor tensor = Tensors.vector(-2, 0);
    assertFalse(coneRegion.test(tensor));
    Scalar distance = coneRegion.distance(tensor);
    assertTrue(Scalars.lessThan(distance, RealScalar.of(0.1)));
  }

  public void testNegativeFail() {
    AssertFail.of(() -> new ConeRegion(Tensors.vector(5, 0, Math.PI / 2), RealScalar.of(-Math.PI)));
  }
}
