// code by jph
package ch.alpine.sophus.ply;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class PolygonNormalizeTest extends TestCase {
  public void testSimple() {
    Tensor polygon = Tensors.fromString("{{0, 0}, {2, 0}, {2, 2}, {0, 2}}");
    Tensor tensor = PolygonNormalize.of(polygon, RealScalar.ONE);
    assertEquals(tensor.toString(), "{{-1/2, -1/2}, {1/2, -1/2}, {1/2, 1/2}, {-1/2, 1/2}}");
  }
}
