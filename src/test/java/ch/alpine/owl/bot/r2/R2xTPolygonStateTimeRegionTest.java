// code by jph
package ch.alpine.owl.bot.r2;

import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.hs.r2.So2Family;
import ch.alpine.sophus.math.BijectionFamily;
import ch.alpine.sophus.ply.CogPoints;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class R2xTPolygonStateTimeRegionTest extends TestCase {
  public void testSimple() {
    Tensor polygon = CogPoints.of(4, RealScalar.of(1.0), RealScalar.of(0.3));
    // ---
    BijectionFamily bijectionFamily = new So2Family(s -> s);
    Region<StateTime> cog0 = new R2xTPolygonStateTimeRegion(polygon, bijectionFamily, null);
    assertTrue(cog0.isMember(new StateTime(Tensors.vector(0, 0), RealScalar.of(0))));
    assertTrue(cog0.isMember(new StateTime(Tensors.vector(0, 0), RealScalar.of(1))));
    assertTrue(cog0.isMember(new StateTime(Tensors.vector(0.8, 0.1), RealScalar.of(0))));
    assertFalse(cog0.isMember(new StateTime(Tensors.vector(0.8, 0.1), RealScalar.of(0.2))));
  }
}
