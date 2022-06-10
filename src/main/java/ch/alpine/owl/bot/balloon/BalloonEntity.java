// code by astoll
package ch.alpine.owl.bot.balloon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import ch.alpine.ascona.util.ren.ImageRender;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.GfxMatrix;
import ch.alpine.owl.ani.api.AbstractCircularEntity;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.util.ren.TrajectoryRender;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.sca.Clips;

/* package */ class BalloonEntity extends AbstractCircularEntity {
  private static final Tensor PARTITION_SCALE = Tensors.vector(2, 2, 1, 1).unmodifiable();
  static final int FLOWRES = 4;
  static final Scalar U_MAX = RealScalar.of(30);
  final static Scalar SPEED_MAX = RealScalar.of(10);
  /** preserve 1[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.of(2);
  private static final Scalar GOAL_RADIUS = RealScalar.of(3);
  // ---
  private final StateSpaceModel stateSpaceModel;
  private BufferedImage bufferedImage;

  /** @param episodeIntegrator
   * @param trajectoryControl
   * @param stateSpaceModel */
  public BalloonEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl, StateSpaceModel stateSpaceModel) {
    super(episodeIntegrator, trajectoryControl);
    add(BalloonFallbackControl.INSTANCE);
    this.stateSpaceModel = stateSpaceModel;
    bufferedImage = ResourceData.bufferedImage("/graphics/hotairballoon.png");
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    return Vector2NormSquared.between(x, y);
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  @Override // from TrajectoryEntity
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    Collection<Tensor> controls = BalloonFlows.of(U_MAX).getFlows(FLOWRES);
    BalloonMinTimeGoalManager balloonMinTimeGoalManager = //
        new BalloonMinTimeGoalManager(Extract2D.FUNCTION.apply(goal), GOAL_RADIUS, SPEED_MAX);
    GoalInterface goalInterface = balloonMinTimeGoalManager.getGoalInterface();
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, stateSpaceModel, RationalScalar.of(1, 5), 3), //
        controls, plannerConstraint, goalInterface);
  }

  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.state(PARTITION_SCALE);
  }

  @Override // from AbstractCircularEntity
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(trajectoryWrap)) {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(trajectoryWrap.trajectory());
      trajectoryRender.setColor(Color.GREEN);
      trajectoryRender.render(geometricLayer, graphics);
    }
    { // indicate current position
      Tensor state = getStateTimeNow().state();
      geometricLayer.pushMatrix(GfxMatrix.translation(state));
      new ImageRender(bufferedImage, CoordinateBoundingBox.of(Stream.of( //
          Clips.interval(-5, 5), //
          Clips.interval(0, 10)))).render(geometricLayer, graphics);
      geometricLayer.popMatrix();
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(64, 128, 64, 192));
      graphics.fill(new Ellipse2D.Double(point.getX() - 2, point.getY() - 2, 5, 5));
    }
  }
}
