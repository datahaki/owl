// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.R2NoiseRegion;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** demo visualizes the detected obstacles */
public class R2NoiseAnimationDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(0.2, 0.2), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    R2Entity r2Entity = new R2Entity(episodeIntegrator, trajectoryControl);
    owlAnimationFrame.add(r2Entity);
    Region<Tensor> region = new R2NoiseRegion(RealScalar.of(0.2));
    TrajectoryRegionQuery trajectoryRegionQuery = CatchyTrajectoryRegionQuery.timeInvariant(region);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    MouseGoal.simple(owlAnimationFrame, r2Entity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenders.create(trajectoryRegionQuery));
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new R2NoiseAnimationDemo().start().jFrame.setVisible(true);
  }
}
