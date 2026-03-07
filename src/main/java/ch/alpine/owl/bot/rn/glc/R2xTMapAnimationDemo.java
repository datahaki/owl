// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.bot.rn.RnPointcloudRegions;
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
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;

/** the obstacle region in the demo is the outside of a rotating letter 'a' */
public class R2xTMapAnimationDemo implements DemoInterface {
  private static final Scalar DELAY = Quantity.of(1.5, "s");

  @Override
  public OwlAnimationFrame getTimerFrame() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        StateSpaceModels.SINGLE_INTEGRATOR, //
        TimeIntegrators.EULER, //
        new StateTime(Tensors.vector(4.5, 5), Quantity.of(0, "s")));
    TrajectoryEntity abstractEntity = new R2xTEntity(episodeIntegrator, DELAY);
    owlAnimationFrame.add(abstractEntity);
    MemberQ imageRegion = ImageRegions.loadFromRepository( //
        "/dubilab/localization/20180122.png", Tensors.vector(10, 10), false);
    MemberQ region = RnPointcloudRegions.from(imageRegion, RealScalar.of(0.15));
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    MouseGoal.simple(owlAnimationFrame.geometricComponent, abstractEntity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenderFactory.create(imageRegion));
    owlAnimationFrame.geometricComponent.setOffset(100, 800);
    return owlAnimationFrame;
  }

  static void main() {
    new R2xTMapAnimationDemo().runStandalone();
  }
}
