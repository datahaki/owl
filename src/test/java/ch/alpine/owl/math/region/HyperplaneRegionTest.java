// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import junit.framework.TestCase;

public class HyperplaneRegionTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = new HyperplaneRegion(Tensors.vector(1, 0), RealScalar.of(5));
    assertFalse(region.isMember(Tensors.vector(0, 0)));
    assertFalse(region.isMember(Tensors.vector(3, 0)));
    assertFalse(region.isMember(Tensors.vector(5, 0)));
    assertFalse(region.isMember(Tensors.vector(8, 0)));
    assertFalse(region.isMember(Tensors.vector(-3, 0)));
    assertTrue(region.isMember(Tensors.vector(-5, 0)));
    assertTrue(region.isMember(Tensors.vector(-8, 0)));
  }

  public void testMore() {
    Region<Tensor> region = new HyperplaneRegion(Tensors.vector(-1, 0), RealScalar.of(10));
    assertFalse(region.isMember(Array.zeros(2)));
    assertFalse(region.isMember(Tensors.vector(9, 0)));
    assertFalse(region.isMember(Tensors.vector(9, -3)));
    assertTrue(region.isMember(Tensors.vector(11, 0)));
    assertTrue(region.isMember(Tensors.vector(11, 7)));
  }

  public void testNormalize() {
    Region<Tensor> region = HyperplaneRegion.normalize(Tensors.vector(-2, 0), RealScalar.of(10));
    assertFalse(region.isMember(Array.zeros(2)));
    assertFalse(region.isMember(Tensors.vector(9, 0)));
    assertFalse(region.isMember(Tensors.vector(9, -3)));
    assertTrue(region.isMember(Tensors.vector(11, 0)));
    assertTrue(region.isMember(Tensors.vector(11, 7)));
  }

  public void testDistance() {
    ImplicitFunctionRegion ifr = HyperplaneRegion.normalize(Tensors.vector(2, 0), RealScalar.of(-10));
    assertTrue(ifr.isMember(Array.zeros(2)));
    assertTrue(ifr.isMember(Tensors.vector(9, 0)));
    assertTrue(ifr.isMember(Tensors.vector(9, -3)));
    assertFalse(ifr.isMember(Tensors.vector(11, 0)));
    assertFalse(ifr.isMember(Tensors.vector(11, 7)));
    assertEquals(ifr.signedDistance(Array.zeros(2)), RealScalar.of(-10));
    assertTrue(ExactScalarQ.of(ifr.signedDistance(Array.zeros(2))));
    Scalar distance = ifr.signedDistance(Tensors.vector(10, 0));
    assertEquals(distance, RealScalar.of(0));
    assertTrue(ExactScalarQ.of(distance));
  }

  public void testDistanceFail() {
    ImplicitFunctionRegion ifr = HyperplaneRegion.normalize(Tensors.vector(2, 0), RealScalar.of(-10));
    AssertFail.of(() -> ifr.signedDistance(Array.zeros(3)));
  }
}
