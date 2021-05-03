// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class BoundedBoxRegionTest extends TestCase {
  public void testSimple() {
    Region<Tensor> r = BoundedBoxRegion.fromCenterAndRadius(Tensors.vector(10, 5), Tensors.vector(2, 5));
    assertTrue(r.isMember(Tensors.vector(10, 5)));
    assertTrue(r.isMember(Tensors.vector(10, 9)));
    assertTrue(r.isMember(Tensors.vector(11, 9)));
    assertTrue(r.isMember(Tensors.vector(9, 9)));
    assertFalse(r.isMember(Tensors.vector(13, 5)));
    assertFalse(r.isMember(Tensors.vector(10, 11)));
    assertTrue(r.isMember(Tensors.vector(12, 5)));
  }
}
