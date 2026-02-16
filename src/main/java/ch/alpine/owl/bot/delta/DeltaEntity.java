// code by jph
package ch.alpine.owl.bot.delta;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.List;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.api.AbstractCircularEntity;
import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.owl.region.BallRegion;
import ch.alpine.owl.region.RegionWithDistance;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.ren.TreeRender;
import ch.alpine.sophis.crv.d2.Extract2D;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.nrm.Vector2NormSquared;

/** class controls delta using {@link StandardTrajectoryPlanner} */
/* package */ class DeltaEntity extends AbstractCircularEntity implements GlcPlannerCallback {
  protected static final Tensor PARTITION_SCALE = Tensors.vector(5, 5).unmodifiable();
  /** preserve 1[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.of(2);
  /** the constants define the control */
  private static final Scalar U_NORM = RealScalar.of(0.6);
  /** resolution of radial controls */
  private static final int U_SIZE = 15;
  private static final Scalar GOAL_RADIUS = RealScalar.of(0.3);
  // ---
  private final TreeRender treeRender = new TreeRender();
  private final ImageGradientInterpolation imageGradientInterpolation;
  private RegionWithDistance<Tensor> goalRegion = null;
  final FixedStateIntegrator fixedStateIntegrator;

  /** @param episodeIntegrator
   * @param trajectoryControl
   * @param imageGradientInterpolation */
  public DeltaEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl, ImageGradientInterpolation imageGradientInterpolation) {
    super(episodeIntegrator, trajectoryControl);
    add(new DeltaCoastingControl(imageGradientInterpolation, U_NORM));
    this.imageGradientInterpolation = imageGradientInterpolation;
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    fixedStateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, stateSpaceModel, Rational.of(1, 5), 4);
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    return Vector2NormSquared.between(x, y);
  }

  @Override
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  /** @param goal
   * @return */
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new BallRegion(Extract2D.FUNCTION.apply(goal), GOAL_RADIUS);
  }

  @Override
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    Collection<Tensor> controls = new DeltaFlows(U_NORM).getFlows(U_SIZE);
    Scalar u_norm = DeltaControls.maxSpeed(controls);
    Scalar maxNormGradient = imageGradientInterpolation.maxNormGradient();
    Scalar maxMove = maxNormGradient.add(u_norm);
    goalRegion = getGoalRegionWithDistance(goal);
    GoalInterface goalInterface = new DeltaMinTimeGoalManager(goalRegion, maxMove);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), //
        FixedStateIntegrator.create( //
            RungeKutta45Integrator.INSTANCE, stateSpaceModel, Rational.of(1, 5), 4),
        controls, plannerConstraint, goalInterface);
  }

  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.state(PARTITION_SCALE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RegionRenders.draw(geometricLayer, graphics, goalRegion);
    // ---
    super.render(geometricLayer, graphics);
    // ---
    treeRender.render(geometricLayer, graphics);
  }

  @Override // from GlcPlannerCallback
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    treeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }
}
