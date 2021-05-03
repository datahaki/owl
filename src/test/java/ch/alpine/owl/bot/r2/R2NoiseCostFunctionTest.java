// code by jph
package ch.alpine.owl.bot.r2;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.HeuristicQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class R2NoiseCostFunctionTest extends TestCase {
  public void testSimple() {
    CostFunction costFunction = new R2NoiseCostFunction(RealScalar.of(0.2));
    assertFalse(HeuristicQ.of(costFunction));
  }

  public void testSerializable() throws Exception {
    CostFunction costFunction = new R2NoiseCostFunction(RealScalar.of(-0.5));
    Serialization.copy(costFunction);
  }
}
