// code by jph
package ch.alpine.owl.bot.rn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;

class RnPointRegionTest {
  @Test
  public void testSimple() {
    RnPointRegion pointRegion = new RnPointRegion(Tensors.vector(1, 2, 3, 4));
    Scalar scalar = pointRegion.distance(Tensors.vector(2, 3, 2, 5));
    assertEquals(scalar, RealScalar.of(2));
    ExactScalarQ.require(scalar);
    assertFalse(pointRegion.test(Tensors.vector(2, 3, 4, 5)));
    assertTrue(pointRegion.test(Tensors.vector(1, 2, 3, 4)));
  }
}
