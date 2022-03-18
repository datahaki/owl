// code by jph
package ch.alpine.owl.bot.rice;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;

public class Rice2GoalManagerTest {
  @Test
  public void testSimple() {
    AssertFail.of(() -> new Rice2GoalManager(null));
  }
}
