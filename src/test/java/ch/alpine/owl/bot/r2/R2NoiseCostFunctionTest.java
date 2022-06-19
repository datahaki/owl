// code by jph
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.HeuristicQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;

class R2NoiseCostFunctionTest {
  @Test
  void testSimple() {
    CostFunction costFunction = new R2NoiseCostFunction(RealScalar.of(0.2));
    assertFalse(HeuristicQ.of(costFunction));
  }

  @Test
  void testSerializable() throws Exception {
    CostFunction costFunction = new R2NoiseCostFunction(RealScalar.of(-0.5));
    Serialization.copy(costFunction);
  }
}
