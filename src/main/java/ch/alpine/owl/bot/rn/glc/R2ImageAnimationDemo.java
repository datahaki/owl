// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** demo shows the use of a cost image that is added to the distance cost
 * which gives an incentive to stay clear of obstacles */
public class R2ImageAnimationDemo implements DemoInterface {
  @Override // from DemoInterface
  public OwlAnimationFrame start() {
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(7, 6), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    R2Entity r2Entity = new R2Entity(episodeIntegrator, trajectoryControl);
    r2Entity.extraCosts.add(r2ImageRegionWrap.costFunction());
    owlAnimationFrame.add(r2Entity);
    Region<Tensor> region = r2ImageRegionWrap.region();
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    MouseGoal.simple(owlAnimationFrame, r2Entity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenders.create(region));
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new R2ImageAnimationDemo().start().jFrame.setVisible(true);
  }
}
