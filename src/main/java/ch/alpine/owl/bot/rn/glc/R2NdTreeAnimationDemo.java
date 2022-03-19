// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.awt.image.BufferedImage;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.bot.rn.RnPointcloudRegions;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;

/** demo shows the use of a cost image that is added to the distance cost
 * which gives an incentive to stay clear of obstacles */
public class R2NdTreeAnimationDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    String path = "/io/track0_100.png";
    BufferedImage bufferedImage = ResourceData.bufferedImage(path);
    Region<Tensor> imageRegion = ImageRegions.from(bufferedImage, Tensors.vector(10, 10), false);
    Region<Tensor> region = RnPointcloudRegions.from(imageRegion, RealScalar.of(0.3));
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(0, 0), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    R2Entity r2Entity = new R2Entity(episodeIntegrator, trajectoryControl);
    owlAnimationFrame.add(r2Entity);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    MouseGoal.simple(owlAnimationFrame, r2Entity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new R2NdTreeAnimationDemo().start().jFrame.setVisible(true);
  }
}
