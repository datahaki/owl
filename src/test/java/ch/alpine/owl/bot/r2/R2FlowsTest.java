// code by jph
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.lie.r2.ConvexHull;

class R2FlowsTest {
  @Test
  void testSimple() {
    int n = 100;
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> flows = r2Flows.getFlows(n);
    assertEquals(flows.size(), n);
    Tensor tflow = Tensor.of(flows.stream());
    Tensor hul = ConvexHull.of(tflow);
    assertEquals(Dimensions.of(tflow), Dimensions.of(hul));
  }

  @Test
  void testFail() {
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    assertThrows(Exception.class, () -> r2Flows.getFlows(2));
  }
}
