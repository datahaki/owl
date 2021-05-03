// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.owl.bot.util.DemoInterfaceHelper;
import junit.framework.TestCase;

public class RLTrajectoryPlanner0DemoTest extends TestCase {
  public void testSimple() {
    DemoInterfaceHelper.brief(new RLTrajectoryPlanner0Demo());
  }

  public void testPresent() {
    assertTrue(RLTrajectoryPlanner0Demo.getBest().isPresent());
  }
}
