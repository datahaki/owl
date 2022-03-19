// code by jph
package ch.alpine.owl.bot.psu;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.List;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.ani.adapter.FallbackControl;
import ch.alpine.owl.ani.api.AbstractCircularEntity;
import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.gui.ren.TreeRender;
import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTimeTensorFunction;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;

/* package */ class PsuEntity extends AbstractCircularEntity implements GlcPlannerCallback {
  private static final Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;
  /** preserve 1[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.ONE;
  // ---
  private final TreeRender treeRender = new TreeRender();

  public PsuEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
    add(FallbackControl.of(Array.zeros(1)));
  }

  @Override // from TensorMetric
  public final Scalar distance(Tensor x, Tensor y) {
    return PsuMetric.INSTANCE.distance(x, y);
  }

  @Override
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  @Override
  public TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    Tensor eta = Tensors.vector(6, 8);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        INTEGRATOR, PsuStateSpaceModel.INSTANCE, RationalScalar.of(1, 4), 5);
    Collection<Tensor> controls = PsuControls.createControls(0.2, 6);
    PsuWrap psuWrap = PsuWrap.INSTANCE;
    GoalInterface goalInterface = PsuGoalManager.of( //
        PsuMetric.INSTANCE, psuWrap.represent(Extract2D.FUNCTION.apply(goal)), RealScalar.of(0.2));
    StateTimeRaster stateTimeRaster = new EtaRaster(eta, StateTimeTensorFunction.state(psuWrap::represent));
    return new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    treeRender.render(geometricLayer, graphics);
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    treeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }
}
