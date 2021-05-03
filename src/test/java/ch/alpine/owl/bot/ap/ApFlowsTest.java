// code by jph
package ch.alpine.owl.bot.ap;

import java.util.Collection;

import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Degree;
import junit.framework.TestCase;

public class ApFlowsTest extends TestCase {
  public void testSimple() {
    FlowsInterface flowsInterface = ApFlows.of(Degree.of(10), Tensors.vector(0, 1, 2));
    Collection<Tensor> collection = flowsInterface.getFlows(10);
    assertEquals(collection.size(), 33);
  }
}
