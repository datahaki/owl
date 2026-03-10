// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.R2NoiseRegion;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owlets.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.math.state.EpisodeIntegrator;
import ch.alpine.owlets.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TrajectoryRegionQuery;
import ch.alpine.sophis.flow.StateSpaceModels;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;

/** demo visualizes the detected obstacles */
public class R2NoiseAnimationDemo implements DemoInterface {
  @Override
  public TimerFrame getWindow() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        StateSpaceModels.SINGLE_INTEGRATOR, //
        TimeIntegrators.EULER, //
        new StateTime(Tensors.vector(0.2, 0.2), Quantity.of(0, "s")));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    R2Entity r2Entity = new R2Entity(episodeIntegrator, trajectoryControl);
    owlAnimationFrame.add(r2Entity);
    MemberQ region = new R2NoiseRegion(RealScalar.of(0.2));
    TrajectoryRegionQuery trajectoryRegionQuery = CatchyTrajectoryRegionQuery.timeInvariant(region);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    MouseGoal.simple(owlAnimationFrame.timerFrame.geometricComponent, r2Entity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenderFactory.create(trajectoryRegionQuery));
    return owlAnimationFrame.timerFrame;
  }

  static void main() {
    new R2NoiseAnimationDemo().runStandalone();
  }
}
