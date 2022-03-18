// code by jph
package ch.alpine.owl.glc.core;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.ScaledLateralAcceleration;
import ch.alpine.owl.bot.se2.Se2LateralAcceleration;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;

public class HeuristicQTest {
  @Test
  public void testSimple() {
    assertFalse(HeuristicQ.of(Se2LateralAcceleration.INSTANCE));
    assertFalse(HeuristicQ.of(new ScaledLateralAcceleration(RealScalar.ONE)));
  }

  @Test
  public void testFail() {
    AssertFail.of(() -> HeuristicQ.of(null));
  }
}
