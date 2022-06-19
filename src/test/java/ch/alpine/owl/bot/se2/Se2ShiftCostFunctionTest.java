// code by jph
package ch.alpine.owl.bot.se2;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;

class Se2ShiftCostFunctionTest {
  @Test
  void testSerializable() throws Exception {
    CostFunction costFunction = new Se2ShiftCostFunction(Quantity.of(100, "CHF"));
    Serialization.copy(costFunction);
  }
}
