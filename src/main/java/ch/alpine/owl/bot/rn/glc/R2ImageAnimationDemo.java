// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.math.state.EpisodeIntegrator;
import ch.alpine.owlets.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.flow.StateSpaceModels;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;

/** demo shows the use of a cost image that is added to the distance cost
 * which gives an incentive to stay clear of obstacles */
public class R2ImageAnimationDemo implements DemoInterface {
  @Override // from DemoInterface
  public TimerFrame getWindow() {
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        StateSpaceModels.SINGLE_INTEGRATOR, //
        TimeIntegrators.EULER, //
        new StateTime(Tensors.vector(7, 6), Quantity.of(0, "s")));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    R2Entity r2Entity = new R2Entity(episodeIntegrator, trajectoryControl);
    r2Entity.extraCosts.add(r2ImageRegionWrap.costFunction());
    owlAnimationFrame.add(r2Entity);
    MemberQ region = r2ImageRegionWrap.region();
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    MouseGoal.simple(owlAnimationFrame.timerFrame.geometricComponent(), r2Entity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenderFactory.create(region));
    return owlAnimationFrame.timerFrame;
  }

  static void main() {
    new R2ImageAnimationDemo().runStandalone();
  }
}
