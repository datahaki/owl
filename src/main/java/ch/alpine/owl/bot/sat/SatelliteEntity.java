// code by jph
package ch.alpine.owl.bot.sat;

import java.awt.Graphics2D;
import java.util.Collection;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.ani.adapter.FallbackControl;
import ch.alpine.owl.ani.api.AbstractCircularEntity;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.nrm.Vector2NormSquared;

/* package */ class SatelliteEntity extends AbstractCircularEntity {
  protected static final Tensor PARTITION_SCALE = Tensors.vector(5, 5, 6, 6).unmodifiable();
  private static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { { .3, 0, 1 }, { -.1, -.1, 1 }, { -.1, +.1, 1 } }).unmodifiable();
  private static final StateSpaceModel STATE_SPACE_MODEL = SatelliteStateSpaceModel.INSTANCE;
  private static final Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;
  // ---
  private final Collection<Tensor> controls;
  public Scalar delayHint = RealScalar.ONE;

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
        FixedStateIntegrator.create(INTEGRATOR, STATE_SPACE_MODEL, RationalScalar.of(1, 12), 4);
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
