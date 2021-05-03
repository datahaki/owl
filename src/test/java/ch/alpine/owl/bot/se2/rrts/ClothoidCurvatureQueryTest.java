// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import junit.framework.TestCase;

public class ClothoidCurvatureQueryTest extends TestCase {
  public void testWidthZeroFail() {
    AssertFail.of(() -> new ClothoidCurvatureQuery(Clips.interval(3, 3)));
  }

  public void testNullFail() {
    AssertFail.of(() -> new ClothoidCurvatureQuery((Clip) null));
  }
}
