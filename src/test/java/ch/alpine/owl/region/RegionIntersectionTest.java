// code by jph
package ch.alpine.owl.region;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;

class RegionIntersectionTest {
  @Test
  void testSimple() {
    MemberQ intersection = MemberQ.all( //
        Arrays.asList( //
            new EllipsoidRegion(Tensors.vector(-2), Tensors.vector(3)), //
            new EllipsoidRegion(Tensors.vector(+2), Tensors.vector(3))));
    assertTrue(intersection.test(Tensors.vector(0)));
    assertTrue(intersection.test(Tensors.vector(0.5)));
    assertTrue(intersection.test(Tensors.vector(1)));
    assertFalse(intersection.test(Tensors.vector(1.2)));
    assertFalse(intersection.test(Tensors.vector(-1.2)));
  }
}
