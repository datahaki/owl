// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.bot.rn.RnPointcloudRegions;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.win.MouseGoal;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** the obstacle region in the demo is the outside of a rotating letter 'a' */
public class R2xTMapAnimationDemo implements DemoInterface {
  private static final Scalar DELAY = RealScalar.of(1.5);

  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(4.5, 5), RealScalar.ZERO));
    TrajectoryEntity abstractEntity = new R2xTEntity(episodeIntegrator, DELAY);
    owlyAnimationFrame.add(abstractEntity);
    Region<Tensor> imageRegion = ImageRegions.loadFromRepository( //
        "/dubilab/localization/20180122.png", Tensors.vector(10, 10), false);
    Region<Tensor> region = RnPointcloudRegions.from(imageRegion, RealScalar.of(0.15));
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    MouseGoal.simple(owlyAnimationFrame, abstractEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlyAnimationFrame.geometricComponent.setOffset(100, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2xTMapAnimationDemo().start().jFrame.setVisible(true);
  }
}
