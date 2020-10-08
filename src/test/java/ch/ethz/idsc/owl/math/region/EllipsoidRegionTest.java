// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.IOException;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class EllipsoidRegionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Region<Tensor> region = Serialization.copy(new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(1, 1)));
    assertTrue(region.isMember(Tensors.vector(10, 5)));
    assertTrue(region.isMember(Tensors.vector(10, 5.5)));
    assertTrue(region.isMember(Tensors.vector(10, 6)));
    assertFalse(region.isMember(Tensors.vector(10, 6.5)));
  }

  public void testSimple2() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(2, 2));
    assertTrue(region.isMember(Tensors.vector(10, 5)));
    assertTrue(region.isMember(Tensors.vector(10, 5.5)));
    assertTrue(region.isMember(Tensors.vector(10, 7)));
    assertTrue(region.isMember(Tensors.vector(12, 5)));
    assertTrue(region.isMember(Tensors.vector(11.2, 6.2)));
    assertFalse(region.isMember(Tensors.vector(10, 7.1)));
    assertFalse(region.isMember(Tensors.vector(10, 7.5)));
  }

  public void testEllipsoid() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(2, 1));
    assertTrue(region.isMember(Tensors.vector(10, 5)));
    assertTrue(region.isMember(Tensors.vector(10, 5.5)));
    assertFalse(region.isMember(Tensors.vector(10, 7)));
    assertTrue(region.isMember(Tensors.vector(12, 5)));
    assertFalse(region.isMember(Tensors.vector(12.1, 5)));
    assertFalse(region.isMember(Tensors.vector(11.2, 6.2)));
    assertFalse(region.isMember(Tensors.vector(10, 6.1)));
    assertFalse(region.isMember(Tensors.vector(10, 7.5)));
  }

  public void testInfty() {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(5, 10), Tensors.vector(1 / 0.0, 2));
    assertEquals(ifr.signedDistance(Tensors.vector(1000, 8)), RealScalar.ZERO);
  }

  public void test1D() {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(10), Tensors.vector(2));
    assertEquals(ifr.signedDistance(Tensors.vector(8)), RealScalar.ZERO);
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    ImplicitFunctionRegion ifr = new EllipsoidRegion(Tensors.vector(10), Tensors.vector(2));
    Serialization.copy(ifr);
  }

  public void testLengthFail() {
    AssertFail.of(() -> new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, 0, 3)));
  }

  public void testNegativeFail() {
    AssertFail.of(() -> new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, -2)));
  }

  public void testZeroFail() {
    AssertFail.of(() -> new EllipsoidRegion(Tensors.vector(10, 3), Tensors.vector(1, 0)));
    AssertFail.of(() -> new EllipsoidRegion(Tensors.vector(10, 2, 3), Tensors.vector(1, 0.0, 3)));
  }
}
