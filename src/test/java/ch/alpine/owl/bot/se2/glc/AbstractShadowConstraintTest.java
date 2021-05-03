// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.Tensor;
import junit.framework.TestCase;

public class AbstractShadowConstraintTest extends TestCase {
  public void testNumeric() {
    Tensor tensor = AbstractShadowConstraint.DIR;
    assertFalse(ExactScalarQ.of(tensor.Get(0)));
    assertFalse(ExactScalarQ.of(tensor.Get(1)));
  }
}
