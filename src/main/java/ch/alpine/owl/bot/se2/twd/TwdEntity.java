// code by jph
package ch.alpine.owl.bot.se2.twd;

import java.awt.Graphics2D;
import java.util.Collection;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.bot.se2.Se2LateralAcceleration;
import ch.alpine.owl.bot.se2.Se2MinTimeGoalManager;
import ch.alpine.owl.bot.se2.Se2Wrap;
import ch.alpine.owl.bot.se2.glc.Se2Entity;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.MultiCostGoalAdapter;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.StateTimeTensorFunction;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.region.So2Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.crv.d2.Arrowhead;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.sca.Sqrt;

/* package */ class TwdEntity extends Se2Entity {
  static final Tensor PARTITION_SCALE = Tensors.of( //
      RealScalar.of(6), RealScalar.of(6), Degree.of(10).reciprocal()).unmodifiable();
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  static final Tensor SHAPE = Arrowhead.of(0.3);

  public static TwdEntity createDuckie(StateTime stateTime) {
    TwdEntity twdEntity = new TwdEntity( //
        stateTime, new TwdTrajectoryControl(), new TwdDuckieFlows(RealScalar.ONE, RealScalar.ONE));
    twdEntity.extraCosts.add(Se2LateralAcceleration.INSTANCE);
    return twdEntity;
  }

  public static TwdEntity createJ2B2(StateTime stateTime) {
    return new TwdEntity( //
        stateTime, new TwdTrajectoryControl(), new TwdForwardFlows(RealScalar.ONE, RealScalar.ONE));
  }

  // ---
  final Collection<Tensor> controls;
  final Scalar goalRadius_xy;
  final Scalar goalRadius_theta;

  /** @param twdConfig
   * @param stateTime initial position */
  protected TwdEntity(StateTime stateTime, TrajectoryControl trajectoryControl, TwdFlows twdConfig) {
    super(stateTime, trajectoryControl);
    controls = twdConfig.getFlows(4);
    Tensor eta = PARTITION_SCALE;
    goalRadius_xy = SQRT2.divide(eta.Get(0));
    goalRadius_theta = SQRT2.divide(eta.Get(2));
  }

  @Override // from TensorMetric
  public Scalar distance(Tensor x, Tensor y) {
    return Vector2Norm.of(Se2Wrap.INSTANCE.difference(x, y)); // non-negative
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.ONE;
  }

  private RegionWithDistance<Tensor> goalRegion = null;

  /** @param goal
   * @return */
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new BallRegion(Extract2D.FUNCTION.apply(goal), goalRadius_xy);
  }

  @Override
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = //
        new Se2ComboRegion(goalRegion, So2Region.periodic(goal.Get(2), goalRadius_theta));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager( //
        se2ComboRegion, controls);
    GoalInterface goalInterface = MultiCostGoalAdapter.of( //
        se2MinTimeGoalManager.getGoalInterface(), //
        extraCosts);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXED_STATE_INTEGRATOR, controls, plannerConstraint, goalInterface);
  }

  @Override
  protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(PARTITION_SCALE, StateTimeTensorFunction.state(Se2Wrap.INSTANCE::represent));
  }

  @Override
  protected Tensor shape() {
    return SHAPE;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RegionRenders.draw(geometricLayer, graphics, goalRegion);
    // ---
    super.render(geometricLayer, graphics);
  }
}
