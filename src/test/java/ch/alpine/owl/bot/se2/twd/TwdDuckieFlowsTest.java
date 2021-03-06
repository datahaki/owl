// code by jph
package ch.alpine.owl.bot.se2.twd;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Round;
import junit.framework.TestCase;

public class TwdDuckieFlowsTest extends TestCase {
  public void testRadNoDuplicates() {
    Scalar ms = Quantity.of(3, "m*s^-1");
    Scalar sa = Quantity.of(0.567, "m*rad^-1");
    FlowsInterface flowsInterface = new TwdDuckieFlows(ms, sa);
    for (int res = 3; res <= 8; ++res) {
      Collection<Tensor> controls = flowsInterface.getFlows(res);
      Set<Tensor> set = new HashSet<>();
      for (Tensor flow : controls) {
        Tensor key = flow.map(Round._3);
        assertTrue(set.add(key));
      }
    }
  }

  public void testNoDuplicates() {
    Scalar ms = Quantity.of(3, "m*s^-1");
    Scalar sa = Quantity.of(0.567, "m");
    FlowsInterface flowsInterface = new TwdDuckieFlows(ms, sa);
    for (int res = 3; res <= 8; ++res) {
      Collection<Tensor> controls = flowsInterface.getFlows(res);
      Set<Tensor> set = new HashSet<>();
      for (Tensor flow : controls) {
        Tensor key = flow.map(Round._3);
        assertTrue(set.add(key));
      }
    }
  }

  public void testSize() throws ClassNotFoundException, IOException {
    FlowsInterface flowsInterface = Serialization.copy(new TwdDuckieFlows(RealScalar.of(3), RealScalar.of(0.567)));
    assertEquals(flowsInterface.getFlows(5).size(), 20);
    assertEquals(flowsInterface.getFlows(7).size(), 28);
    assertEquals(flowsInterface.getFlows(8).size(), 32);
  }
}
