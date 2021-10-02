// code by jph
package ch.alpine.owl.bot.delta;

import java.awt.image.BufferedImage;

import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.win.MouseGoal;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;

public class DeltaAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    // ---
    Scalar amp = RealScalar.of(-.05);
    Tensor range = Tensors.vector(12.6, 9.1).unmodifiable();
    ImageGradientInterpolation imageGradientInterpolation = //
        ImageGradientInterpolation.nearest(ResourceData.of("/io/delta_uxy.png"), range, amp);
    BufferedImage bufferedImage = ResourceData.bufferedImage("/io/delta_free.png");
    Region<Tensor> region = ImageRegion.of(bufferedImage, range, true);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    StateTime stateTime = new StateTime(Tensors.vector(10, 3.5), RealScalar.ZERO);
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        new DeltaStateSpaceModel(imageGradientInterpolation), EulerIntegrator.INSTANCE, stateTime);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    DeltaEntity deltaEntity = new DeltaEntity(episodeIntegrator, trajectoryControl, imageGradientInterpolation);
    MouseGoal.simple(owlyAnimationFrame, deltaEntity, plannerConstraint);
    owlyAnimationFrame.add(deltaEntity);
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    owlyAnimationFrame.addBackground(DeltaHelper.vectorFieldRender(stateSpaceModel, range, region, RealScalar.of(0.5)));
    owlyAnimationFrame.geometricComponent.setOffset(50, 600);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) throws Exception {
    new DeltaAnimationDemo().start().jFrame.setVisible(true);
  }
}
