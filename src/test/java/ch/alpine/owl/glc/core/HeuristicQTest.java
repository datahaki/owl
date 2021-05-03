// code by jph
package ch.alpine.owl.glc.core;

import ch.alpine.owl.bot.se2.ScaledLateralAcceleration;
import ch.alpine.owl.bot.se2.Se2LateralAcceleration;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class HeuristicQTest extends TestCase {
  public void testSimple() {
    assertFalse(HeuristicQ.of(Se2LateralAcceleration.INSTANCE));
    assertFalse(HeuristicQ.of(new ScaledLateralAcceleration(RealScalar.ONE)));
  }

  public void testFail() {
    AssertFail.of(() -> HeuristicQ.of(null));
  }
}
