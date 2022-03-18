// code by jph
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.api.BijectionFamily;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.crv.d2.CogPoints;
import ch.alpine.sophus.hs.r2.So2Family;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class R2xTPolygonStateTimeRegionTest {
  @Test
  public void testSimple() {
    Tensor polygon = CogPoints.of(4, RealScalar.of(1.0), RealScalar.of(0.3));
    // ---
    BijectionFamily bijectionFamily = new So2Family(s -> s);
    Region<StateTime> cog0 = new R2xTPolygonStateTimeRegion(polygon, bijectionFamily, null);
    assertTrue(cog0.test(new StateTime(Tensors.vector(0, 0), RealScalar.of(0))));
    assertTrue(cog0.test(new StateTime(Tensors.vector(0, 0), RealScalar.of(1))));
    assertTrue(cog0.test(new StateTime(Tensors.vector(0.8, 0.1), RealScalar.of(0))));
    assertFalse(cog0.test(new StateTime(Tensors.vector(0.8, 0.1), RealScalar.of(0.2))));
  }
}
