// code by jph
package ch.alpine.owl.ani.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.ani.api.EntityControl;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class TemporalTrajectoryControlTest {
  @Test
  public void testFallback() {
    EntityControl entityControl = FallbackControl.of(Tensors.vector(1, 2));
    Tensor control = entityControl.control(new StateTime(Tensors.vector(3, 4), RealScalar.of(2)), RealScalar.of(3)).get();
    assertEquals(control, Tensors.vector(1, 2));
  }

  @Test
  public void testFutureTrajectoryUntil() {
    TrajectoryControl trajectoryControl = TemporalTrajectoryControl.createInstance();
    List<TrajectorySample> trajectory = //
        trajectoryControl.getFutureTrajectoryUntil(new StateTime(Tensors.vector(3, 4), RealScalar.of(2)), RealScalar.of(3));
    assertEquals(trajectory.size(), 1);
    assertEquals(trajectory.get(0).stateTime().time(), RealScalar.of(5));
  }

  @Test
  public void testSetTrajectoryNull() {
    TrajectoryControl trajectoryControl = TemporalTrajectoryControl.createInstance();
    trajectoryControl.trajectory(null);
  }
}
