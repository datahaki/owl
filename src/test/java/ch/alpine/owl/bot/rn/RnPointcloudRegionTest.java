// code by jph
package ch.alpine.owl.bot.rn;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;

class RnPointcloudRegionTest {
  @Test
  void testEmpty() {
    Region<Tensor> region = RnPointcloudRegion.of(Tensors.empty(), RealScalar.ONE);
    assertFalse(region.test(Tensors.vector(2, 2.5)));
    assertFalse(region.test(Tensors.vector(2, 2)));
    assertFalse(region.test(Tensors.vector(2, 1)));
    assertFalse(region.test(Tensors.vector(7, 1)));
  }

  @Test
  void testSingle2D() {
    Region<Tensor> region = RnPointcloudRegion.of(Tensors.matrix(new Number[][] { { 2, 3 } }), RealScalar.ONE);
    assertTrue(region.test(Tensors.vector(2, 2.5)));
    assertTrue(region.test(Tensors.vector(2, 2)));
    assertFalse(region.test(Tensors.vector(2, 1)));
    assertFalse(region.test(Tensors.vector(7, 1)));
  }

  @Test
  void testTwo2D() {
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

  @Test
  void testRadiusFail() {
    RnPointcloudRegion.of(Tensors.empty(), RealScalar.of(1.0));
    assertThrows(Exception.class, () -> RnPointcloudRegion.of(Tensors.empty(), RealScalar.of(-1.0)));
  }

  @Test
  void testNonMatrix() {
    assertThrows(Exception.class, () -> RnPointcloudRegion.of(Array.zeros(3, 3, 3), RealScalar.of(1.0)));
  }
}
