// code by jph
package ch.alpine.owl.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.ExactScalarQ;

class HyperplaneRegionTest {
  @Test
  void testSimple() {
    Region<Tensor> region = new HyperplaneRegion(Tensors.vector(1, 0), RealScalar.of(5));
    assertFalse(region.test(Tensors.vector(0, 0)));
    assertFalse(region.test(Tensors.vector(3, 0)));
    assertFalse(region.test(Tensors.vector(5, 0)));
    assertFalse(region.test(Tensors.vector(8, 0)));
    assertFalse(region.test(Tensors.vector(-3, 0)));
    assertTrue(region.test(Tensors.vector(-5, 0)));
    assertTrue(region.test(Tensors.vector(-8, 0)));
  }

  @Test
  void testMore() {
    Region<Tensor> region = new HyperplaneRegion(Tensors.vector(-1, 0), RealScalar.of(10));
    assertFalse(region.test(Array.zeros(2)));
    assertFalse(region.test(Tensors.vector(9, 0)));
    assertFalse(region.test(Tensors.vector(9, -3)));
    assertTrue(region.test(Tensors.vector(11, 0)));
    assertTrue(region.test(Tensors.vector(11, 7)));
  }

  @Test
  void testNormalize() {
    Region<Tensor> region = HyperplaneRegion.normalize(Tensors.vector(-2, 0), RealScalar.of(10));
    assertFalse(region.test(Array.zeros(2)));
    assertFalse(region.test(Tensors.vector(9, 0)));
    assertFalse(region.test(Tensors.vector(9, -3)));
    assertTrue(region.test(Tensors.vector(11, 0)));
    assertTrue(region.test(Tensors.vector(11, 7)));
  }

  @Test
  void testDistance() {
    ImplicitFunctionRegion ifr = HyperplaneRegion.normalize(Tensors.vector(2, 0), RealScalar.of(-10));
    assertTrue(ifr.test(Array.zeros(2)));
    assertTrue(ifr.test(Tensors.vector(9, 0)));
    assertTrue(ifr.test(Tensors.vector(9, -3)));
    assertFalse(ifr.test(Tensors.vector(11, 0)));
    assertFalse(ifr.test(Tensors.vector(11, 7)));
    assertEquals(ifr.signedDistance(Array.zeros(2)), RealScalar.of(-10));
    assertTrue(ExactScalarQ.of(ifr.signedDistance(Array.zeros(2))));
    Scalar distance = ifr.signedDistance(Tensors.vector(10, 0));
    assertEquals(distance, RealScalar.of(0));
    assertTrue(ExactScalarQ.of(distance));
  }

  @Test
  void testDistanceFail() {
    ImplicitFunctionRegion ifr = HyperplaneRegion.normalize(Tensors.vector(2, 0), RealScalar.of(-10));
    assertThrows(Exception.class, () -> ifr.signedDistance(Array.zeros(3)));
  }
}
