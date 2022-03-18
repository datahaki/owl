// code by jph
package ch.alpine.owl.math.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

public class EllipsoidRegionTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Region<Tensor> region = Serialization.copy(new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(1, 1)));
    assertTrue(region.test(Tensors.vector(10, 5)));
    assertTrue(region.test(Tensors.vector(10, 5.5)));
    assertTrue(region.test(Tensors.vector(10, 6)));
    assertFalse(region.test(Tensors.vector(10, 6.5)));
  }

  @Test
  public void testSimple2() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(2, 2));
    assertTrue(region.test(Tensors.vector(10, 5)));
    assertTrue(region.test(Tensors.vector(10, 5.5)));
    assertTrue(region.test(Tensors.vector(10, 7)));
    assertTrue(region.test(Tensors.vector(12, 5)));
    assertTrue(region.test(Tensors.vector(11.2, 6.2)));
    assertFalse(region.test(Tensors.vector(10, 7.1)));
    assertFalse(region.test(Tensors.vector(10, 7.5)));
  }

  @Test
  public void testEllipsoid() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(2, 1));
    assertTrue(region.test(Tensors.vector(10, 5)));
    assertTrue(region.test(Tensors.vector(10, 5.5)));
    assertFalse(region.test(Tensors.vector(10, 7)));
    assertTrue(region.test(Tensors.vector(12, 5)));
    assertFalse(region.test(Tensors.vector(12.1, 5)));
    assertFalse(region.test(Tensors.vector(11.2, 6.2)));
    assertFalse(region.test(Tensors.vector(10, 6.1)));
    assertFalse(region.test(Tensors.vector(10, 7.5)));
  }

  @Test
  public void testInfty() {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(5, 10), Tensors.vector(1 / 0.0, 2));
    assertEquals(ifr.signedDistance(Tensors.vector(1000, 8)), RealScalar.ZERO);
  }

  @Test
  public void test1D() {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(10), Tensors.vector(2));
    assertEquals(ifr.signedDistance(Tensors.vector(8)), RealScalar.ZERO);
  }

  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(10), Tensors.vector(2));
    Serialization.copy(ifr);
  }

  @Test
  public void testLengthFail() {
    AssertFail.of(() -> new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, 0, 3)));
  }

  @Test
  public void testNegativeFail() {
    AssertFail.of(() -> new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, -2)));
  }

  @Test
  public void testZeroFail() {
    AssertFail.of(() -> new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, 0)));
    AssertFail.of(() -> new EllipsoidRegion(Tensors.vector(10, 2, 3), Tensors.vector(1, 0.0, 3)));
  }
}
