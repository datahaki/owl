// code by jph
package ch.alpine.owl.bot.rice;

import ch.alpine.tensor.RealScalar;
import junit.framework.TestCase;

public class Rice2StateSpaceModelTest extends TestCase {
  public void testFails() {
    Rice2StateSpaceModel.of(RealScalar.ZERO);
    Rice2StateSpaceModel.of(RealScalar.of(-1));
  }
}
