// code by jph
package ch.alpine.owl.bot.rice;

import ch.alpine.owl.math.AssertFail;
import junit.framework.TestCase;

public class Rice2GoalManagerTest extends TestCase {
  public void testSimple() {
    AssertFail.of(() -> new Rice2GoalManager(null));
  }
}
