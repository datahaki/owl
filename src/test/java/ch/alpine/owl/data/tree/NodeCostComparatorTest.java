// code by jph
package ch.alpine.owl.data.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.owlets.data.tree.NodeCostComparator;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

class NodeCostComparatorTest {
  @Test
  void testSimple() {
    GlcNode root = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ZERO), _ -> RealScalar.ZERO);
    assertEquals(NodeCostComparator.INSTANCE.compare(root, root), 0);
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> NodeCostComparator.INSTANCE.compare(null, null));
  }
}
