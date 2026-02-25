// code by jph
package ch.alpine.owl.data.tree;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owlets.data.tree.NodesAssert;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

class NodesAssertTest {
  @Test
  void testAllLeaf() {
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ONE), _ -> RealScalar.ZERO);
    NodesAssert.allLeaf(List.of(glcNode));
  }
}
