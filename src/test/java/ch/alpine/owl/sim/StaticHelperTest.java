// code by jph
package ch.alpine.owl.sim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;

class StaticHelperTest {
  @Test
  public void testSimple() {
    for (int resolution = 2; resolution < 10; ++resolution) {
      Tensor localPoints = StaticHelper.create(resolution);
      assertEquals(localPoints.length(), resolution * resolution);
    }
  }
}
