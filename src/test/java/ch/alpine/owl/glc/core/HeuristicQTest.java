// code by jph
package ch.alpine.owl.glc.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.ScaledLateralAcceleration;
import ch.alpine.owl.bot.se2.Se2LateralAcceleration;
import ch.alpine.tensor.RealScalar;

class HeuristicQTest {
  @Test
  void testSimple() {
    assertFalse(HeuristicQ.of(Se2LateralAcceleration.INSTANCE));
    assertFalse(HeuristicQ.of(new ScaledLateralAcceleration(RealScalar.ONE)));
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> HeuristicQ.of(null));
  }
}
