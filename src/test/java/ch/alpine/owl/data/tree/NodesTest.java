// code by jph
package ch.alpine.owl.data.tree;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.HeuristicFunction;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class NodesTest extends TestCase {
  public void testGetParent() {
    HeuristicFunction heuristicFunction = new HeuristicFunction() {
      @Override
      public Scalar minCostToGoal(Tensor x) {
        return RealScalar.ZERO;
      }
    };
    GlcNode node1 = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO), heuristicFunction);
    GlcNode node2 = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO), heuristicFunction);
    node1.insertEdgeTo(node2);
    assertEquals(Nodes.getParent(node2, 0), node2);
    assertEquals(Nodes.getParent(node2, 1), node1);
    assertEquals(Nodes.getParent(node2, 2), node1);
  }

  public void testNotConnected() {
    GlcNode root1 = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ZERO), x -> RealScalar.ZERO);
    assertTrue(Nodes.areConnected(root1, root1));
    GlcNode root2 = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), RealScalar.ZERO), x -> RealScalar.ZERO);
    assertFalse(Nodes.areConnected(root1, root2));
  }

  public void testFail() {
    AssertFail.of(() -> Nodes.rootFrom(null));
    AssertFail.of(() -> Nodes.listFromRoot(null));
    AssertFail.of(() -> Nodes.listToRoot(null));
  }
}
