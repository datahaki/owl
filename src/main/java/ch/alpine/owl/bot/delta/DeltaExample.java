// code by jph
package ch.alpine.owl.bot.delta;

import java.util.Collection;

import ch.alpine.ascony.reg.BufferedImageRegion;
import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owlets.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.sophis.reg.BallRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clips;

/** simple animation of small boat driving upstream, or downstream in a river delta */
/* package */ class DeltaExample {
  // private static final StateIntegrator STATE_INTEGRATOR = FixedStateIntegrator.create( //
  // RungeKutta45Integrator.INSTANCE, Rational.of(1, 10), 4);
  private static final Tensor RANGE = Tensors.vector(9, 6.5);
  // private static final Tensor OBSTACLE_IMAGE = ; //
  private static final CoordinateBoundingBox coordinateBoundingBox = CoordinateBoundingBox.of( //
      Clips.positive(RANGE.Get(0)), Clips.positive(RANGE.Get(1)));
  static final MemberQ REGION = new BufferedImageRegion( //
      ResourceData.bufferedImage("/io/delta_free.png"), coordinateBoundingBox, true);
  private static final PlannerConstraint PLANNER_CONSTRAINT = //
      new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(REGION));
  private static final Scalar MAX_INPUT = RealScalar.ONE;
  static final BallRegion SPHERICAL_REGION = //
      new BallRegion(Tensors.vector(2.1, 0.3), RealScalar.of(0.3));
  private static final StateTimeRaster STATE_TIME_RASTER = EtaRaster.state(Tensors.vector(8, 8));
  // ---
  private final ImageGradientInterpolation imageGradientInterpolation;
  private final StateSpaceModel stateSpaceModel;
  final TrajectoryPlanner trajectoryPlanner;

  public DeltaExample(Scalar amp) {
    imageGradientInterpolation = //
        ImageGradientInterpolation.linear(Import.of("/io/delta_uxy.png"), RANGE, amp);
    stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    Scalar maxNormGradient = imageGradientInterpolation.maxNormGradient();
    Scalar maxMove = maxNormGradient.add(MAX_INPUT);
    Collection<Tensor> controls = new DeltaFlows(MAX_INPUT).getFlows(25);
    GoalInterface goalInterface = new DeltaMinTimeGoalManager(SPHERICAL_REGION, maxMove);
    trajectoryPlanner = new StandardTrajectoryPlanner( //
        STATE_TIME_RASTER, new FixedStateIntegrator( //
            TimeIntegrators.RK45, stateSpaceModel, Quantity.of(Rational.of(1, 10), "s"), 4),
        controls, PLANNER_CONSTRAINT, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(8.8, 0.5), RealScalar.ZERO));
  }

  public RenderInterface vf(double scale) {
    return StaticHelper.vectorFieldRender(stateSpaceModel, RANGE, REGION, RealScalar.of(scale));
  }
}
