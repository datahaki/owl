// code by jph
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.HeuristicQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;

public class R2NoiseCostFunctionTest {
  @Test
  public void testSimple() {
    CostFunction costFunction = new R2NoiseCostFunction(RealScalar.of(0.2));
    assertFalse(HeuristicQ.of(costFunction));
  }

  @Test
  public void testSerializable() throws Exception {
    CostFunction costFunction = new R2NoiseCostFunction(RealScalar.of(-0.5));
    Serialization.copy(costFunction);
  }
}
