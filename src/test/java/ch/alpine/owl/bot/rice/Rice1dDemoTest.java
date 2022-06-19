// code by jph
package ch.alpine.owl.bot.rice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.HeuristicAssert;
import ch.alpine.owl.glc.core.TrajectoryPlanner;

class Rice1dDemoTest {
  @Test
  void testFindGoal() {
    TrajectoryPlanner trajectoryPlanner = Rice1dDemo.simple();
    assertTrue(trajectoryPlanner.getBest().isPresent());
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
  }
}
