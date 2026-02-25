// code by astoll
package ch.alpine.owl.bot.ap;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;

class ApDemoTest {
  final static Tensor INITIAL_TENSOR = ApDemo.INITIAL;

  @Test
  void testApDemo() {
    StandardTrajectoryPlanner standardTrajectoryPlanner = ApTrajectoryPlanner.apStandardTrajectoryPlanner();
    standardTrajectoryPlanner.insertRoot(new StateTime(INITIAL_TENSOR, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(standardTrajectoryPlanner);
    glcExpand.findAny(15000);
    assertTrue(standardTrajectoryPlanner.getBest().isPresent());
  }
}
