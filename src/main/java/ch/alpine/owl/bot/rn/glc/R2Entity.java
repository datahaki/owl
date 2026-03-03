// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.adapter.FallbackControl;
import ch.alpine.owl.ani.api.AbstractCircularEntity;
import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.rn.RnMinTimeGoalManager;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.ren.TreeRender;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.MultiCostGoalAdapter;
import ch.alpine.owlets.glc.core.CostFunction;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.EpisodeIntegrator;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.TrajectorySample;
import ch.alpine.sophis.crv.d2.Extract2D;
import ch.alpine.sophis.flow.StateSpaceModels;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.sophis.reg.BallRegion;
import ch.alpine.sophis.reg.RegionWithDistance;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.qty.Quantity;

/** omni-directional movement with constant speed
 * 
 * the implementation chooses certain values */
/* package */ class R2Entity extends AbstractCircularEntity implements GlcPlannerCallback {
  protected static final Tensor PARTITION_SCALE = Tensors.vector(8, 8).unmodifiable();
  public static final FixedStateIntegrator FIXED_STATE_INTEGRATOR = new FixedStateIntegrator( //
      TimeIntegrators.EULER, StateSpaceModels.SINGLE_INTEGRATOR, Quantity.of(Rational.of(1, 12), "s"), 4);
  // ---
  private final TreeRender treeRender = new TreeRender();
  /** extra cost functions, for instance to prevent cutting corners */
  public final Collection<CostFunction> extraCosts = new LinkedList<>();
  protected final R2Flows r2Flows = new R2Flows(Quantity.of(1, "s^-1"));
  protected RegionWithDistance<Tensor> goalRegion = null;

  /** @param state initial position of entity */
  public R2Entity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
    add(FallbackControl.of(r2Flows.stayPut()));
  }

  @Override
  public Scalar distance(Tensor x, Tensor y) {
    return Vector2NormSquared.between(x, y); // non-negative
  }

  @Override
  public Scalar delayHint() {
    /** preserve 0.5[s] of the former trajectory
     * planning should not exceed that duration, otherwise
     * the entity may not be able to follow a planned trajectory */
    return Quantity.of(0.5, "s");
  }

  /** @param goal
   * @return */
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    Tensor partitionScale = PARTITION_SCALE;
    Scalar goalRadius = RealScalar.of(Math.sqrt(2.0)).divide(partitionScale.Get(0));
    return new BallRegion(Extract2D.FUNCTION.apply(goal), goalRadius);
  }

  @Override
  public TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    Collection<Tensor> controls = createControls(); // TODO OWL API design no good
    goalRegion = getGoalRegionWithDistance(goal);
    GoalInterface goalInterface = MultiCostGoalAdapter.of( //
        RnMinTimeGoalManager.create(goalRegion, controls), //
        extraCosts);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXED_STATE_INTEGRATOR, controls, //
        plannerConstraint, goalInterface);
  }

  Collection<Tensor> createControls() {
    /** 36 corresponds to 10[Degree] resolution */
    return r2Flows.getFlows(36);
  }

  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.state(PARTITION_SCALE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RegionRenderFactory.draw(geometricLayer, graphics, goalRegion);
    super.render(geometricLayer, graphics);
    treeRender.render(geometricLayer, graphics);
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    treeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }
}
