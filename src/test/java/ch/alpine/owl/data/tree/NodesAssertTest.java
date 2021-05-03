// code by jph
package ch.alpine.owl.data.tree;

import java.util.Collections;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class NodesAssertTest extends TestCase {
  public void testAllLeaf() {
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ONE), x -> RealScalar.ZERO);
    NodesAssert.allLeaf(Collections.singletonList(glcNode));
  }
}
