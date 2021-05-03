// code by jph
package ch.alpine.owl.data.tree;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class NodeCostComparatorTest extends TestCase {
  public void testSimple() {
    GlcNode root = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ZERO), x -> RealScalar.ZERO);
    assertEquals(NodeCostComparator.INSTANCE.compare(root, root), 0);
  }

  public void testFail() {
    AssertFail.of(() -> NodeCostComparator.INSTANCE.compare(null, null));
  }
}
