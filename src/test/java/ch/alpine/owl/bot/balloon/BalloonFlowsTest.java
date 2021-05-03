// code by astoll
package ch.alpine.owl.bot.balloon;

import java.util.Collection;

import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import junit.framework.TestCase;

public class BalloonFlowsTest extends TestCase {
  public void testSimple() {
    FlowsInterface flowsInterface = BalloonFlows.of(RealScalar.of(10));
    Collection<Tensor> collection = flowsInterface.getFlows(0);
    assertEquals(collection.size(), 2);
  }

  public void testFail() {
    FlowsInterface flowsInterface = BalloonFlows.of(RealScalar.of(10));
    AssertFail.of(() -> flowsInterface.getFlows(-1));
  }
}
