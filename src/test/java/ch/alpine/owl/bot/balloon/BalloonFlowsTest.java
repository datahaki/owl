// code by astoll
package ch.alpine.owl.bot.balloon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;

public class BalloonFlowsTest {
  @Test
  public void testSimple() {
    FlowsInterface flowsInterface = BalloonFlows.of(RealScalar.of(10));
    Collection<Tensor> collection = flowsInterface.getFlows(0);
    assertEquals(collection.size(), 2);
  }

  @Test
  public void testFail() {
    FlowsInterface flowsInterface = BalloonFlows.of(RealScalar.of(10));
    AssertFail.of(() -> flowsInterface.getFlows(-1));
  }
}
