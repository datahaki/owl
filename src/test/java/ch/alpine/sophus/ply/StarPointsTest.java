// code by jph
package ch.alpine.sophus.ply;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

public class StarPointsTest extends TestCase {
  public void testSimple() {
    Tensor polygon = StarPoints.of(4, RealScalar.ONE, RealScalar.of(0.3));
    assertEquals(polygon.length(), 8);
    Sign.requirePositive(PolygonArea.of(polygon));
  }
}
