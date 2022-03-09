// code by astoll
package ch.alpine.owl.bot.ap;

import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import junit.framework.TestCase;

public class ApDemoTest extends TestCase {
  final static Tensor INITIAL_TENSOR = ApDemo.INITIAL;

  public void testApDemo() {
    StandardTrajectoryPlanner standardTrajectoryPlanner = ApTrajectoryPlanner.apStandardTrajectoryPlanner();
    standardTrajectoryPlanner.insertRoot(new StateTime(INITIAL_TENSOR, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(standardTrajectoryPlanner);
    glcExpand.findAny(15000);
    assertTrue(standardTrajectoryPlanner.getBest().isPresent());
  }
}
