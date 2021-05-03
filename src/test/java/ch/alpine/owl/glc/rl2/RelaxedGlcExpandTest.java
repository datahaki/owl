// code by jph
package ch.alpine.owl.glc.rl2;

import java.util.Objects;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class RelaxedGlcExpandTest extends TestCase {
  public void testExpand() {
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = TestHelper.createPlanner();
    Objects.requireNonNull(relaxedTrajectoryPlanner.getStateIntegrator());
    assertTrue(relaxedTrajectoryPlanner.getQueue().isEmpty());
    Objects.requireNonNull(relaxedTrajectoryPlanner.getBest());
    assertTrue(relaxedTrajectoryPlanner.getRelaxedDomainQueueMap().isEmpty());
    Tensor stateRoot = Tensors.vector(0, 0);
    relaxedTrajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    RelaxedGlcExpand relaxedGlcExpand = new RelaxedGlcExpand(relaxedTrajectoryPlanner);
    assertFalse(relaxedGlcExpand.isOptimal());
    relaxedGlcExpand.findAny(100);
    assertTrue(relaxedGlcExpand.getExpandCount() < 100);
    assertFalse(relaxedGlcExpand.isOptimal());
    relaxedGlcExpand.untilOptimal(1000);
    System.out.println(relaxedGlcExpand.getExpandCount());
    if (1800 < relaxedGlcExpand.getExpandCount()) {
      System.out.println("relaxedGlcExpand.getExpandCount()==" + relaxedGlcExpand.getExpandCount());
      throw new RuntimeException();
    }
    assertTrue(relaxedGlcExpand.isOptimal());
  }
}
