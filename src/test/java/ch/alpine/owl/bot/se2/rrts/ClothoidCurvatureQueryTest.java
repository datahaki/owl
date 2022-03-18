// code by jph
package ch.alpine.owl.bot.se2.rrts;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public class ClothoidCurvatureQueryTest {
  @Test
  public void testWidthZeroFail() {
    AssertFail.of(() -> new ClothoidCurvatureQuery(Clips.interval(3, 3)));
  }

  @Test
  public void testNullFail() {
    AssertFail.of(() -> new ClothoidCurvatureQuery((Clip) null));
  }
}
