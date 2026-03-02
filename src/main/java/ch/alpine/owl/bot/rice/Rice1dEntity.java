// code by jph
package ch.alpine.owl.bot.rice;

import java.awt.Graphics2D;
import java.util.Collection;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.adapter.FallbackControl;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.crv.d2.Extract2D;
import ch.alpine.sophis.flow.Integrator;
import ch.alpine.sophis.flow.Integrators;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.reg.EllipsoidRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.qty.Quantity;

/* package */ class Rice1dEntity extends RiceBaseEntity {
  private static final Integrator INTEGRATOR = Integrators.RK4;
  // ---
  private final StateSpaceModel stateSpaceModel;
  private final Collection<Tensor> controls;

  /** @param state initial position of entity */
  public Rice1dEntity(Scalar mu, Tensor state, TrajectoryControl trajectoryControl, Collection<Tensor> controls) {
    super( //
        new SimpleEpisodeIntegrator(Rice2StateSpaceModel.of(mu), INTEGRATOR, new StateTime(state, RealScalar.ZERO)), //
        trajectoryControl);
    add(FallbackControl.of(Array.zeros(1)));
    this.stateSpaceModel = Rice2StateSpaceModel.of(mu);
    this.controls = controls;
  }

  @Override
  public Scalar delayHint() {
    return Quantity.of(0.5, "s");
  }

  @Override
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    Tensor partitionScale = Tensors.vector(8, 8);
    StateIntegrator stateIntegrator = //
        new FixedStateIntegrator(INTEGRATOR, stateSpaceModel, Quantity.of(Rational.of(1, 12), "s"), 4);
    GoalInterface goalInterface = new Rice1GoalManager(new EllipsoidRegion(Extract2D.FUNCTION.apply(goal), Tensors.vector(0.2, 0.3)));
    StateTimeRaster stateTimeRaster = EtaRaster.state(partitionScale);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, plannerConstraint, goalInterface);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    treeRender.render(geometricLayer, graphics);
  }
}
