// code by jph
package ch.alpine.owl.glc.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class StateTimeTrajectoriesTest extends TestCase {
  public void testTimeIncrement() {
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), Quantity.of(3, "s")), x -> RealScalar.ZERO);
    List<StateTime> trajectory = Arrays.asList( //
        new StateTime(Tensors.vector(1, 2), Quantity.of(4, "s")), //
        new StateTime(Tensors.vector(1, 2), Quantity.of(5, "s")));
    Scalar scalar = StateTimeTrajectories.timeIncrement(glcNode, trajectory);
    assertEquals(scalar, Quantity.of(2, "s"));
    assertTrue(ExactScalarQ.of(scalar));
  }

  public void testDeltaTimes() {
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2), Quantity.of(3, "s")), x -> RealScalar.ZERO);
    List<StateTime> trajectory = Arrays.asList( //
        new StateTime(Tensors.vector(1, 2), Quantity.of(4, "s")), //
        new StateTime(Tensors.vector(1, 2), Quantity.of(7, "s")));
    Tensor deltaTimes = StateTimeTrajectories.deltaTimes(glcNode, trajectory);
    assertEquals(deltaTimes, Tensors.fromString("{1[s], 3[s]}"));
    ExactTensorQ.require(deltaTimes);
  }

  public void testDeltatime() {
    GlcNode glcNode = GlcNode.of(null, new StateTime(Tensors.vector(1, 2), RealScalar.ONE), RealScalar.ZERO, RealScalar.ZERO);
    List<StateTime> trajectory = new ArrayList<>();
    trajectory.add(new StateTime(Tensors.vector(0, 5), RealScalar.of(3)));
    trajectory.add(new StateTime(Tensors.vector(5, 5), RealScalar.of(4)));
    Tensor dts = StateTimeTrajectories.deltaTimes(glcNode, trajectory);
    assertEquals(dts, Tensors.vector(2, 1));
  }
}
