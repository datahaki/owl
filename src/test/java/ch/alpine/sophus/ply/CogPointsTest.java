// code by jph
package ch.alpine.sophus.ply;

import java.util.Arrays;

import ch.alpine.sophus.ply.d2.ConvexHull;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

public class CogPointsTest extends TestCase {
  public void testSimple() {
    Tensor polygon = CogPoints.of(10, RealScalar.of(10.2), RealScalar.of(3.2));
    assertEquals(Dimensions.of(polygon), Arrays.asList(40, 2));
    Sign.requirePositive(PolygonArea.of(polygon));
    Tensor convex = ConvexHull.of(polygon);
    assertEquals(Dimensions.of(convex), Arrays.asList(20, 2));
  }

  public void testToggled() {
    Tensor tensor = CogPoints.of(10, RealScalar.of(10.2), RealScalar.of(30.2));
    assertEquals(Dimensions.of(tensor), Arrays.asList(40, 2));
    Tensor convex = ConvexHull.of(tensor);
    assertEquals(Dimensions.of(convex), Arrays.asList(20, 2));
  }
}
