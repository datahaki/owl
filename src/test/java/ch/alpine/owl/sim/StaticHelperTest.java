// code by jph
package ch.alpine.owl.sim;

import ch.alpine.tensor.Tensor;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    for (int resolution = 2; resolution < 10; ++resolution) {
      Tensor localPoints = StaticHelper.create(resolution);
      assertEquals(localPoints.length(), resolution * resolution);
    }
  }
}
