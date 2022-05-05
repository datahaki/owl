// code by jph
package ch.alpine.owl.bot.rice;

import java.awt.Graphics2D;
import java.util.Collection;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.adapter.FallbackControl;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.flow.MidpointIntegrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.lie.r2.AngleVector;

/* package */ class Rice2dEntity extends RiceBaseEntity {
  private static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { { .3, 0, 1 }, { -.1, -.1, 1 }, { -.1, +.1, 1 } }).unmodifiable();
  private static final Integrator INTEGRATOR = MidpointIntegrator.INSTANCE;
  // ---
  private final StateSpaceModel stateSpaceModel;
  private final Collection<Tensor> controls;
  // ---
  public Scalar delayHint = RealScalar.ONE;

  /** @param state initial position of entity */
  public Rice2dEntity(Scalar mu, Tensor state, TrajectoryControl trajectoryControl, Collection<Tensor> controls) {
    super( //
        new SimpleEpisodeIntegrator(Rice2StateSpaceModel.of(mu), INTEGRATOR, new StateTime(state, RealScalar.ZERO)), //
        trajectoryControl);
    add(FallbackControl.of(Array.zeros(2)));
    stateSpaceModel = Rice2StateSpaceModel.of(mu);
    this.controls = controls;
  }

  @Override
  public final Scalar delayHint() {
    return delayHint;
  }

  @Override
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    Tensor partitionScale = Tensors.vector(3, 3, 6, 6);
    StateIntegrator stateIntegrator = //
        FixedStateIntegrator.create(INTEGRATOR, stateSpaceModel, RationalScalar.of(1, 12), 4);
    Tensor center = Join.of(Extract2D.FUNCTION.apply(goal), AngleVector.of(goal.Get(2)).multiply(RealScalar.of(0.8)));
    GoalInterface goalInterface = new Rice2GoalManager(new EllipsoidRegion(center, Tensors.vector(0.5, 0.5, 0.4, 0.4)));
    StateTimeRaster stateTimeRaster = EtaRaster.state(partitionScale);
    return new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, plannerConstraint, goalInterface);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    treeRender.render(geometricLayer, graphics);
    {
      // FIXME OWL API this is the wrong place to draw the mouse
      // Tensor xya = geometricLayer.getMouseSe2State();
      // geometricLayer.pushMatrix(Se2Matrix.of(xya));
      // graphics.setColor(new Color(0, 128, 255, 192));
      // graphics.fill(geometricLayer.toPath2D(SHAPE));
      // geometricLayer.popMatrix();
    }
  }
}
