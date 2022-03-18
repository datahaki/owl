// code by jph
package ch.alpine.owl.bot.se2.glc;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.Tensor;

public class AbstractShadowConstraintTest {
  @Test
  public void testNumeric() {
    Tensor tensor = AbstractShadowConstraint.DIR;
    assertFalse(ExactScalarQ.of(tensor.Get(0)));
    assertFalse(ExactScalarQ.of(tensor.Get(1)));
  }
}
