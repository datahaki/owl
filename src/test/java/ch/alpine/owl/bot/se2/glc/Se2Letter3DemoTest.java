// code by jph
package ch.alpine.owl.bot.se2.glc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.util.bot.DemoInterfaceHelper;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.qty.Degree;

class Se2Letter3DemoTest {
  @Test
  void testSimple() {
    DemoInterfaceHelper.brief(new Se2Letter3Demo());
  }

  @Test
  void testDegree() {
    assertEquals(RealScalar.of(Math.PI / 6), Degree.of(180 / 6));
    assertEquals(RealScalar.of(Math.PI / 6), Degree.of(30));
  }
}
