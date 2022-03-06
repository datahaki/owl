// code by jph
package ch.alpine.owl.math.region;

import java.util.Arrays;

import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class RegionIntersectionTest extends TestCase {
  public void testSimple() {
    Region<Tensor> intersection = RegionIntersection.wrap( //
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
