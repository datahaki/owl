// code by jph
package ch.alpine.owl.math.state;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

public class TimeDependentRegionTest {
  @Test
  public void testSimple() {
    Region<StateTime> region = //
        new TimeDependentRegion(new EllipsoidRegion(Tensors.vector(1, 2), Tensors.vector(3, 4)));
    assertTrue(region.test(new StateTime(Tensors.vector(1), RealScalar.of(2))));
    assertFalse(region.test(new StateTime(Tensors.vector(1), RealScalar.of(7))));
  }
}
