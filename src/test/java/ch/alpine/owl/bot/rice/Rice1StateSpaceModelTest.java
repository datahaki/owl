// code by jph
package ch.alpine.owl.bot.rice;

import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class Rice1StateSpaceModelTest extends TestCase {
  public void testFormerFails() {
    Rice1StateSpaceModel.of(RealScalar.ZERO);
    Rice1StateSpaceModel.of(RealScalar.of(-1));
  }
}
