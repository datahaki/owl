// code by jph 
package ch.alpine.owl.bot.rn;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import junit.framework.TestCase;

public class RnPointcloudRegionTest extends TestCase {
  public void testEmpty() {
    Region<Tensor> region = RnPointcloudRegion.of(Tensors.empty(), RealScalar.ONE);
    assertFalse(region.test(Tensors.vector(2, 2.5)));
    assertFalse(region.test(Tensors.vector(2, 2)));
    assertFalse(region.test(Tensors.vector(2, 1)));
    assertFalse(region.test(Tensors.vector(7, 1)));
  }

  public void testSingle2D() {
    Region<Tensor> region = RnPointcloudRegion.of(Tensors.matrix(new Number[][] { { 2, 3 } }), RealScalar.ONE);
    assertTrue(region.test(Tensors.vector(2, 2.5)));
    assertTrue(region.test(Tensors.vector(2, 2)));
    assertFalse(region.test(Tensors.vector(2, 1)));
    assertFalse(region.test(Tensors.vector(7, 1)));
  }

  public void testTwo2D() {
    Region<Tensor> region = RnPointcloudRegion.of(Tensors.matrix(new Number[][] { //
        { 2, 3 }, //
        { 7, 1 } //
    }), RealScalar.ONE);
    assertTrue(region.test(Tensors.vector(2, 2.5)));
    assertTrue(region.test(Tensors.vector(2, 2)));
    assertFalse(region.test(Tensors.vector(2, 1)));
    assertTrue(region.test(Tensors.vector(7, 1)));
    assertTrue(region.test(Tensors.vector(7, 2)));
    assertFalse(region.test(Tensors.vector(8, 2)));
  }

  public void testRadiusFail() {
    RnPointcloudRegion.of(Tensors.empty(), RealScalar.of(1.0));
    AssertFail.of(() -> RnPointcloudRegion.of(Tensors.empty(), RealScalar.of(-1.0)));
  }

  public void testNonMatrix() {
    AssertFail.of(() -> RnPointcloudRegion.of(Array.zeros(3, 3, 3), RealScalar.of(1.0)));
  }
}
