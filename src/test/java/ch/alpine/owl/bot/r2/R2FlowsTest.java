// code by jph
package ch.alpine.owl.bot.r2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.lie.r2.ConvexHull;

public class R2FlowsTest {
  @Test
  public void testSimple() {
    int n = 100;
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> flows = r2Flows.getFlows(n);
    assertEquals(flows.size(), n);
    Tensor tflow = Tensor.of(flows.stream());
    Tensor hul = ConvexHull.of(tflow);
    assertEquals(Dimensions.of(tflow), Dimensions.of(hul));
  }

  @Test
  public void testFail() {
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    AssertFail.of(() -> r2Flows.getFlows(2));
  }
}
