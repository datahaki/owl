// code by astoll
package ch.alpine.owl.bot.balloon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;

class BalloonFlowsTest {
  @Test
  void testSimple() {
    FlowsInterface flowsInterface = BalloonFlows.of(RealScalar.of(10));
    Collection<Tensor> collection = flowsInterface.getFlows(0);
    assertEquals(collection.size(), 2);
  }

  @Test
  void testFail() {
    FlowsInterface flowsInterface = BalloonFlows.of(RealScalar.of(10));
    assertThrows(Exception.class, () -> flowsInterface.getFlows(-1));
  }
}
