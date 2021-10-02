// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class NegativeHalfspaceRegionTest extends TestCase {
  public void testSimple() {
    Region<Tensor> r = new NegativeHalfspaceRegion(1);
    assertFalse(r.test(Tensors.vector(1, +1, 1)));
    assertTrue(r.test(Tensors.vector(1, -1, 1)));
  }
}
