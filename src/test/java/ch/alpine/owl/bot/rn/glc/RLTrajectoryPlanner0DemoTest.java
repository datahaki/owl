// code by jph
package ch.alpine.owl.bot.rn.glc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.util.DemoInterfaceHelper;

public class RLTrajectoryPlanner0DemoTest {
  @Test
  public void testSimple() {
    DemoInterfaceHelper.brief(new RLTrajectoryPlanner0Demo());
  }

  @Test
  public void testPresent() {
    assertTrue(RLTrajectoryPlanner0Demo.getBest().isPresent());
  }
}
