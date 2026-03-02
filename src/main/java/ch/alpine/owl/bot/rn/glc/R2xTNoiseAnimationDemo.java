// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.R2xTNoiseStateTimeRegion;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.math.flow.EulerIntegrator;
import ch.alpine.owlets.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owlets.math.state.EpisodeIntegrator;
import ch.alpine.owlets.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

// TODO OWL ALG the visualization of the demo is poor
public class R2xTNoiseAnimationDemo implements DemoInterface {
  private static final Scalar DELAY = Quantity.of(0.5, "s");

  @Override
  public OwlAnimationFrame getTimerFrame() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(0.2, 0.2), Quantity.of(0, "s")));
    TrajectoryEntity trajectoryEntity = new R2xTEntity(episodeIntegrator, DELAY);
    owlAnimationFrame.add(trajectoryEntity);
    Region<StateTime> region = new R2xTNoiseStateTimeRegion(RealScalar.of(0.5));
    PlannerConstraint plannerConstraint = RegionConstraints.stateTime(region);
    MouseGoal.simple(owlAnimationFrame, trajectoryEntity, plannerConstraint);
    return owlAnimationFrame;
  }

  static void main() {
    new R2xTNoiseAnimationDemo().runStandalone();
  }
}
