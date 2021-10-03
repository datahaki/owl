// code by astoll
package ch.alpine.owl.bot.balloon;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;

public class BalloonAnimationDemo implements DemoInterface {
  @Override // from DemoInterface
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlyAnimationFrame = new OwlAnimationFrame();
    owlyAnimationFrame.geometricComponent.setModel2Pixel(Tensors.fromString("{{7.5, 0, 100}, {0, -7.5, 800}, {0, 0, 1}}"));
    PlannerConstraint plannerConstraint = new BalloonPlannerConstraint(BalloonEntity.SPEED_MAX);
    BalloonStateSpaceModel balloonStateSpaceModel = BalloonStateSpaceModels.defaultWithoutUnits();
    StateTime stateTime = new StateTime(Tensors.vector(0, 150, 10, 10), RealScalar.ZERO);
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        balloonStateSpaceModel, EulerIntegrator.INSTANCE, stateTime);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    BalloonEntity balloonEntity = new BalloonEntity(episodeIntegrator, trajectoryControl, balloonStateSpaceModel);
    MouseGoal.simple(owlyAnimationFrame, balloonEntity, plannerConstraint);
    Tensor range = Tensors.vector(500, 100).unmodifiable();
    Region<Tensor> imageRegion = ImageRegion.of(ResourceData.bufferedImage("/io/mountainChain.png"), range, true);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlyAnimationFrame.add(balloonEntity);
    owlyAnimationFrame.addBackground(AxesRender.INSTANCE);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) throws Exception {
    new BalloonAnimationDemo().start().jFrame.setVisible(true);
  }
}
