// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.R2xTNoiseStateTimeRegion;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;

// LONGTERM the visualization of the demo is poor
public class R2xTNoiseAnimationDemo implements DemoInterface {
  private static final Scalar DELAY = RealScalar.of(0.5);

  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(0.2, 0.2), RealScalar.ZERO));
    TrajectoryEntity trajectoryEntity = new R2xTEntity(episodeIntegrator, DELAY);
    owlAnimationFrame.add(trajectoryEntity);
    Region<StateTime> region = new R2xTNoiseStateTimeRegion(RealScalar.of(0.5));
    PlannerConstraint plannerConstraint = RegionConstraints.stateTime(region);
    MouseGoal.simple(owlAnimationFrame, trajectoryEntity, plannerConstraint);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new R2xTNoiseAnimationDemo().start().jFrame.setVisible(true);
  }
}
