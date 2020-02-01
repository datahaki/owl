// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class R2EntityTest extends TestCase {
  public void testSimple() {
    Tensor ux = Tensors.vector(1, 0);
    List<TrajectorySample> trajectory = new ArrayList<>();
    trajectory.add(TrajectorySample.head(new StateTime(Tensors.vector(0, 0), RealScalar.ZERO)));
    trajectory.add(new TrajectorySample(new StateTime(Tensors.vector(1, 0), RealScalar.ONE), ux));
    trajectory.add(new TrajectorySample(new StateTime(Tensors.vector(2, 0), RealScalar.of(2)), ux));
    // ---
    /* {
     * AbstractEntity abstractEntity = new R2Entity(Tensors.vector(0, 0));
     * abstractEntity.setTrajectory(trajectory);
     * int index = abstractEntity.indexOfPassedTrajectorySample(trajectory);
     * assertEquals(index, 0);
     * }
     * {
     * AbstractEntity abstractEntity = new R2Entity(Tensors.vector(0.5, 0));
     * abstractEntity.setTrajectory(trajectory);
     * int index = abstractEntity.indexOfPassedTrajectorySample(trajectory);
     * assertEquals(index, 0);
     * }
     * {
     * AbstractEntity abstractEntity = new R2Entity(Tensors.vector(0.7, 0));
     * abstractEntity.setTrajectory(trajectory);
     * int index = abstractEntity.indexOfPassedTrajectorySample(trajectory);
     * assertEquals(index, 1);
     * }
     * {
     * AbstractEntity abstractEntity = new R2Entity(Tensors.vector(1.3, 0));
     * abstractEntity.setTrajectory(trajectory);
     * int index = abstractEntity.indexOfPassedTrajectorySample(trajectory);
     * assertEquals(index, 1);
     * }
     * {
     * AbstractEntity abstractEntity = new R2Entity(Tensors.vector(1.7, 0));
     * abstractEntity.setTrajectory(trajectory);
     * int index = abstractEntity.indexOfPassedTrajectorySample(trajectory);
     * assertEquals(index, 2);
     * }
     * {
     * AbstractEntity abstractEntity = new R2Entity(Tensors.vector(1.9, 0));
     * abstractEntity.setTrajectory(trajectory);
     * int index = abstractEntity.indexOfPassedTrajectorySample(trajectory);
     * assertEquals(index, 2);
     * } */
  }

  public void testTail() {
    Tensor state = Tensors.vector(0.7, 0);
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(state, RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    R2Entity abstractEntity = new R2Entity(episodeIntegrator, trajectoryControl);
    StateTime st = abstractEntity.getStateTimeNow();
    assertEquals(st.state(), state);
    assertEquals(st.time(), RealScalar.ZERO); // <- specific value == 0 is not strictly required
  }
}
