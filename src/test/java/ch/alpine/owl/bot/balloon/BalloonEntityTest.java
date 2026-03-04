// code by astoll
package ch.alpine.owl.bot.balloon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.EpisodeIntegrator;
import ch.alpine.owlets.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

class BalloonEntityTest {
  private static final BalloonStateSpaceModel BALLOON_STATE_SPACE_MODEL = BalloonStateSpaceModels.defaultWithoutUnits();
  private static final StateTime START = new StateTime(Tensors.vector(0, 10, 0, 0.5), RealScalar.ZERO);
  private static final EpisodeIntegrator EPISODE_INTEGRATOR = new SimpleEpisodeIntegrator( //
      BALLOON_STATE_SPACE_MODEL, TimeIntegrators.EULER, START);

  static BalloonEntity createEntity() {
    return new BalloonEntity(EPISODE_INTEGRATOR, new EuclideanTrajectoryControl(), BALLOON_STATE_SPACE_MODEL);
  }

  @Test
  void testDistanceSimple() {
    Tensor x = Tensors.vector(-1, 2, 1, 0);
    Tensor y = Tensors.vector(3, 2, 4, 0);
    Scalar expected = RealScalar.of(25);
    BalloonEntity balloonEntity = createEntity();
    assertEquals(expected, balloonEntity.distance(x, y));
  }

  @Test
  void testDistanceWithUnits() {
    Tensor x = Tensors.fromString("{2[m], 2[m]}");
    Tensor y = Tensors.fromString("{4[m], 2[m]}");
    Scalar expected = Quantity.of(4, "m^2");
    BalloonEntity balloonEntity = createEntity();
    assertEquals(expected, balloonEntity.distance(x, y));
  }

  @Test
  void testDelayHint() {
    BalloonEntity balloonEntity = createEntity();
    assertEquals(balloonEntity.delayHint(), Quantity.of(2, "s"));
  }

  @Test
  void testCreateTrajectoryPlanner() {
    Tensor goal = Tensors.vector(-30, 0);
    Scalar vertSpeedMax = RealScalar.of(4);
    PlannerConstraint plannerConstraint = new BalloonPlannerConstraint(vertSpeedMax);
    BalloonEntity balloonEntity = createEntity();
    TrajectoryPlanner trajectoryPlanner = balloonEntity.createTreePlanner(plannerConstraint, goal);
    assertInstanceOf(StandardTrajectoryPlanner.class, trajectoryPlanner);
    trajectoryPlanner.insertRoot(START);
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(10_000);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    // assertTrue(optional.isPresent());
  }

  @Test
  void testStateTimeRaster() {
    BalloonEntity balloonEntity = createEntity();
    StateTimeRaster stateTimeRaster = balloonEntity.stateTimeRaster();
    assertInstanceOf(EtaRaster.class, stateTimeRaster);
  }
}
