// code by jph
package ch.alpine.owl.bot.r2;

import java.util.Collection;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.sophus.ply.d2.ConvexHull;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class R2FlowsTest extends TestCase {
  public void testSimple() {
    int n = 100;
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> flows = r2Flows.getFlows(n);
    assertEquals(flows.size(), n);
    Tensor tflow = Tensor.of(flows.stream());
    Tensor hul = ConvexHull.of(tflow);
    assertEquals(Dimensions.of(tflow), Dimensions.of(hul));
  }

  public void testFail() {
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    AssertFail.of(() -> r2Flows.getFlows(2));
  }
}
