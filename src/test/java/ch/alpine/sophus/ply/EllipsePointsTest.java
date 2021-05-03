// code by jph
package ch.alpine.sophus.ply;

import java.util.Arrays;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class EllipsePointsTest extends TestCase {
  public void testScaled() {
    int n = 11;
    Tensor tensor = EllipsePoints.of(n, RealScalar.of(2), RealScalar.of(0.5));
    assertEquals(Dimensions.of(tensor), Arrays.asList(n, 2));
  }
}
