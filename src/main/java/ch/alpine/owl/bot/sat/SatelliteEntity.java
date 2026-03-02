// code by jph
package ch.alpine.owl.bot.sat;

import java.awt.Graphics2D;
import java.util.Collection;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.adapter.FallbackControl;
import ch.alpine.owl.ani.api.AbstractCircularEntity;
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
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.qty.Quantity;

/* package */ class SatelliteEntity extends AbstractCircularEntity {
  protected static final Tensor PARTITION_SCALE = Tensors.vector(5, 5, 6, 6).unmodifiable();
  private static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { { .3, 0, 1 }, { -.1, -.1, 1 }, { -.1, +.1, 1 } }).unmodifiable();
  private static final StateSpaceModel STATE_SPACE_MODEL = SatelliteStateSpaceModel.INSTANCE;
  private static final Integrator INTEGRATOR = Integrators.RK45;
  // ---
  private final Collection<Tensor> controls;
  public Scalar delayHint = Quantity.of(1, "s");

  /** @param state initial position of entity */
  public SatelliteEntity(Tensor state, TrajectoryControl trajectoryControl, Collection<Tensor> controls) {
    super( //
        new SimpleEpisodeIntegrator(STATE_SPACE_MODEL, INTEGRATOR, new StateTime(state, RealScalar.ZERO)), //
        trajectoryControl);
    add(FallbackControl.of(Array.zeros(2)));
    this.controls = controls;
  }

  @Override
  public Scalar distance(Tensor x, Tensor y) {
    return Vector2NormSquared.between(x, y); // non-negative
  }

  @Override
  public final Scalar delayHint() {
    return delayHint;
  }

  @Override
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    StateIntegrator stateIntegrator = //
        new FixedStateIntegrator(INTEGRATOR, STATE_SPACE_MODEL, Quantity.of(Rational.of(1, 12), "s"), 4);
    Tensor center = Join.of(Extract2D.FUNCTION.apply(goal), Array.zeros(2));
    GoalInterface goalInterface = SatelliteGoalManager.create( //
        center, Tensors.vector(0.5, 0.5, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    StateTimeRaster stateTimeRaster = EtaRaster.state(PARTITION_SCALE);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, plannerConstraint, goalInterface);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    {
      // FIXME OWL API this is the wrong place to draw the mouse!
      // Tensor xya = geometricLayer.getMouseSe2State();
      // geometricLayer.pushMatrix(Se2Matrix.of(xya));
      // graphics.setColor(new Color(0, 128, 255, 192));
      // graphics.fill(geometricLayer.toPath2D(SHAPE));
      // geometricLayer.popMatrix();
    }
  }
}
