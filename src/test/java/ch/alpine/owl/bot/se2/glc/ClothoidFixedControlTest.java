// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class ClothoidFixedControlTest extends TestCase {
  public void testNullFail() {
    AssertFail.of(() -> new ClothoidFixedControl(null, RealScalar.of(2)));
    AssertFail.of(() -> new ClothoidFixedControl(RealScalar.of(2), null));
  }
}
