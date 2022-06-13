// code by jph
package ch.alpine.owl.bot.ap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Degree;

class ApFlowsTest {
  @Test
  void testSimple() {
    FlowsInterface flowsInterface = ApFlows.of(Degree.of(10), Tensors.vector(0, 1, 2));
    Collection<Tensor> collection = flowsInterface.getFlows(10);
    assertEquals(collection.size(), 33);
  }
}
