// code by jph
package ch.alpine.owl.bot.se2.glc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.bridge.lang.Unitless;
import ch.alpine.tensor.RealScalar;

class Se2Letter3DemoTest {
  @Test
  void testDegree() {
    assertEquals(RealScalar.of(Math.PI / 6), Unitless.DEGREES.of(180 / 6));
    assertEquals(RealScalar.of(Math.PI / 6), Unitless.DEGREES.of(30));
  }
}
