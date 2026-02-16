// code by jph
package ch.alpine.owl.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;

class BallRegionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Region<Tensor> region = Serialization.copy(new BallRegion(Tensors.vector(1, 1), RealScalar.ONE));
    assertTrue(region.test(Tensors.vector(1, 0)));
    assertTrue(region.test(Tensors.vector(0, 1)));
    assertFalse(region.test(Tensors.vector(2, 0)));
    assertFalse(region.test(Tensors.vector(0, 2)));
  }

  @Test
  void testPoint() {
    Region<Tensor> region = new BallRegion(Tensors.vector(1, 1), RealScalar.ZERO);
    assertTrue(region.test(Tensors.vector(1, 1)));
  }

  @Test
  void testDistance() {
    BallRegion region = new BallRegion(Tensors.vector(1, 1), RealScalar.ZERO);
    assertEquals(region.signedDistance(Tensors.vector(11, 1)), RealScalar.of(10));
  }

  @Test
  void testCenter() {
    BallRegion sr = new BallRegion(Tensors.vector(1, 2), RealScalar.of(5));
    assertEquals(sr.center(), Tensors.vector(1, 2));
    assertEquals(sr.radius(), RealScalar.of(5));
  }

  @Test
  void testQuantity() {
    RegionWithDistance<Tensor> regionWithDistance = new BallRegion(Tensors.fromString("{10[m], 20[m]}"), Quantity.of(5, "m"));
    assertTrue(regionWithDistance.test(Tensors.fromString("{11[m], 19[m]}")));
    Scalar scalar = regionWithDistance.distance(Tensors.fromString("{10[m], 0[m]}"));
    assertEquals(scalar, Quantity.of(15, "m"));
    assertTrue(ExactScalarQ.of(scalar));
  }

  @Test
  void testSignedDistance() {
    ImplicitFunctionRegion implicitFunctionRegion = new BallRegion(Tensors.fromString("{10[m], 20[m]}"), Quantity.of(5, "m"));
    assertTrue(implicitFunctionRegion.test(Tensors.fromString("{11[m], 19[m]}")));
    Scalar scalar = implicitFunctionRegion.signedDistance(Tensors.fromString("{11[m], 20[m]}"));
    assertEquals(scalar, Quantity.of(-4, "m"));
    assertTrue(ExactScalarQ.of(scalar));
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> new BallRegion(RealScalar.ZERO, RealScalar.ONE));
    assertThrows(Exception.class, () -> new BallRegion(Tensors.vector(1, 2), RealScalar.ONE.negate()));
  }
}
