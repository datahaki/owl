// code by jph
package ch.alpine.owl.rrts.adapter;

import java.io.IOException;

import ch.alpine.owl.bot.se2.rrts.ClothoidContinuityCostFunction;
import ch.alpine.owl.rrts.core.TransitionCostFunction;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class ComboTransitionCostFunctionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TransitionCostFunction transitionCostFunction = ComboTransitionCostFunction.of( //
        ClothoidContinuityCostFunction.INSTANCE, //
        LengthCostFunction.INSTANCE);
    Serialization.copy(transitionCostFunction);
  }
}
