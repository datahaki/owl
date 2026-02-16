// code by jph
package ch.alpine.owl.bot.se2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.region.EllipsoidRegion;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;

class Se2PointsVsRegionTest {
  @Test
  void testSimple() {
    Tensor center = Tensors.vector(1, 2);
    MemberQ region = new EllipsoidRegion(center, Tensors.vector(0.1, 0.1));
    Tensor xya = Tensors.vector(4, 5, 6);
    Tensor point = Tensors.vector(7, 8);
    MemberQ region2 = new Se2PointsVsRegion(Tensors.of(point), region);
    assertFalse(region2.test(xya));
  }
}
