// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.GfxMatrix;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.bot.se2.Se2MinTimeGoalManager;
import ch.alpine.owl.bot.se2.Se2ShiftCostFunction;
import ch.alpine.owl.bot.se2.Se2Wrap;
import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.MultiCostGoalAdapter;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.region.So2Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeTensorFunction;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.red.ScalarSummaryStatistics;
import ch.alpine.tensor.sca.pow.Sqrt;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
public class CarEntity extends Se2Entity {
  public static final Tensor PARTITION_SCALE = Tensors.of( //
      RealScalar.of(5), RealScalar.of(5), Degree.of(10).reciprocal()).unmodifiable();
  static final Scalar SPEED = RealScalar.of(1.0);
  static final Scalar MAX_TURNING_PLAN = Degree.of(45);
  static final Scalar LOOKAHEAD = RealScalar.of(0.5);
  /** the pure pursuit controller is permitted a slightly higher turning rate "m^-1"
   * than the planner, to overcome small imprecisions when following the trajectory */
  public static final Scalar MAX_TURNING_RATE = Degree.of(50);
  public static final FlowsInterface CARFLOWS = Se2CarFlows.standard(SPEED, MAX_TURNING_PLAN);
  private static final Scalar SQRT2 = Sqrt.of(RealScalar.of(2));
  private static final Scalar SHIFT_PENALTY = RealScalar.of(0.4);
  // ---
  public static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { //
          { .2, +.07 }, //
          { .25, +.0 }, //
          { .2, -.07 }, //
          { -.1, -.07 }, //
          { -.1, +.07 } //
      }).unmodifiable();

  public static TrajectoryControl createPurePursuitControl() {
    return new PurePursuitControl(LOOKAHEAD, MAX_TURNING_RATE);
  }

  // ---
  public static CarEntity createDefault(StateTime stateTime) {
    return new CarEntity(stateTime, //
        createPurePursuitControl(), //
        PARTITION_SCALE, CARFLOWS, SHAPE);
  }

  // ---
  protected final Collection<Tensor> controls;
  public final Tensor goalRadius;
  final Tensor partitionScale;
  private final Tensor shape;
  protected final TrajectoryControl trajectoryControl; // TODO OWL API design is despicable

  /** extra cost functions, for instance
   * 1) to penalize switching gears
   * 2) to prevent cutting corners
   * 
   * @param stateTime initial position */
  public CarEntity(StateTime stateTime, TrajectoryControl trajectoryControl, Tensor partitionScale, FlowsInterface carFlows, Tensor shape) {
    super(stateTime, trajectoryControl);
    this.trajectoryControl = trajectoryControl;
    // new SimpleEpisodeIntegrator(Se2StateSpaceModel.INSTANCE, Se2CarIntegrator.INSTANCE, stateTime));
    controls = carFlows.getFlows(9);
    final Scalar goalRadius_xy = SQRT2.divide(PARTITION_SCALE.Get(0));
    final Scalar goalRadius_theta = SQRT2.divide(PARTITION_SCALE.Get(2));
    goalRadius = Tensors.of(goalRadius_xy, goalRadius_xy, goalRadius_theta).unmodifiable();
    extraCosts.add(new Se2ShiftCostFunction(SHIFT_PENALTY));
    this.partitionScale = partitionScale.unmodifiable();
    this.shape = shape.copy().unmodifiable();
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(1.5);
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    // Se2Wrap.INSTANCE.difference uses the logarithm internally and is proportional
    // to the geodesic connection between x and y that allows side slip
    return Vector2Norm.of(Se2Wrap.INSTANCE.difference(x, y)); // non-negative
  }

  protected RegionWithDistance<Tensor> goalRegion = null;

  /** @param goal
   * @return */
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new BallRegion(Extract2D.FUNCTION.apply(goal), goalRadius.Get(0));
  }

  @Override
  public TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, So2Region.periodic(goal.Get(2), goalRadius.Get(2)));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = MultiCostGoalAdapter.of(se2MinTimeGoalManager.getGoalInterface(), extraCosts);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXED_STATE_INTEGRATOR, controls, plannerConstraint, goalInterface);
  }

  @Override // from Se2Entity
  protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(partitionScale, StateTimeTensorFunction.state(Se2Wrap.INSTANCE::represent));
  }

  @Override // from Se2Entity
  protected final Tensor shape() {
    return shape;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RegionRenders.draw(geometricLayer, graphics, goalRegion);
    // ---
    super.render(geometricLayer, graphics);
    // ---
    if (trajectoryControl instanceof TrajectoryTargetRender) {
      StateTime stateTime = getStateTimeNow();
      Tensor matrix = GfxMatrix.of(stateTime.state());
      geometricLayer.pushMatrix(matrix);
      graphics.setColor(Color.RED);
      ((TrajectoryTargetRender) trajectoryControl).toTarget(geometricLayer).ifPresent(graphics::draw);
      geometricLayer.popMatrix();
    }
  }

  public final Tensor coords_X() {
    ScalarSummaryStatistics scalarSummaryStatistics = //
        shape.stream().map(tensor -> tensor.Get(0)).collect(ScalarSummaryStatistics.collector());
    return Subdivide.increasing(scalarSummaryStatistics.getClip(), 2);
  }
}
