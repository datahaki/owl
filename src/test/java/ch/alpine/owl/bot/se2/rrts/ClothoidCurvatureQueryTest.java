// code by jph
package ch.alpine.owl.bot.se2.rrts;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class ClothoidCurvatureQueryTest {
  @Test
  void testWidthZeroFail() {
    assertThrows(Exception.class, () -> new ClothoidCurvatureQuery(Clips.interval(3, 3)));
  }

  @Test
  void testNullFail() {
    assertThrows(Exception.class, () -> new ClothoidCurvatureQuery((Clip) null));
  }
}
