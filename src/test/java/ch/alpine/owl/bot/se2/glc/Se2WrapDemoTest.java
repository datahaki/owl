// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.bot.se2.Se2CoveringWrap;
import ch.alpine.owl.bot.se2.Se2Wrap;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.core.CheckedTrajectoryPlanner;
import ch.alpine.owl.glc.core.HeuristicAssert;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.math.region.So2Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class Se2WrapDemoTest extends TestCase {
  public void testSe2Wrap() {
    TrajectoryPlanner trajectoryPlanner = CheckedTrajectoryPlanner.wrap( //
        Se2WrapDemo.createPlanner(Se2Wrap.INSTANCE, So2Region.periodic(RealScalar.ZERO, RealScalar.of(0.3))));
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0.1, 0, 0), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(20);
    assertEquals(glcExpand.getExpandCount(), 20);
    glcExpand.findAny(200);
    assertTrue(glcExpand.getExpandCount() < 100);
    assertTrue(trajectoryPlanner.getBest().isPresent());
    HeuristicAssert.check(trajectoryPlanner);
  }

  public void testSe2CoveringWrap() {
    TrajectoryPlanner trajectoryPlanner = //
        CheckedTrajectoryPlanner.wrap(Se2WrapDemo.createPlanner(Se2CoveringWrap.INSTANCE, So2Region.covering(RealScalar.ZERO, RealScalar.of(0.3))));
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0.1, 0, 0), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(20);
    assertEquals(glcExpand.getExpandCount(), 20);
    glcExpand.findAny(10_000);
    assertTrue(glcExpand.getExpandCount() < 2000);
    assertTrue(trajectoryPlanner.getBest().isPresent());
    HeuristicAssert.check(trajectoryPlanner);
  }
}
