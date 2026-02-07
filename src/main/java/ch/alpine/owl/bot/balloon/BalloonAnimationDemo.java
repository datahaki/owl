// code by astoll
package ch.alpine.owl.bot.balloon;

import ch.alpine.ascony.ren.AxesRender;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.region.BufferedImageRegion;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.sca.Clips;

public class BalloonAnimationDemo implements DemoInterface {
  @Override // from DemoInterface
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    owlAnimationFrame.geometricComponent.setModel2Pixel(Tensors.fromString("{{1.5, 0, 100}, {0, -1.5, 600}, {0, 0, 1}}"));
    PlannerConstraint plannerConstraint = new BalloonPlannerConstraint(BalloonEntity.SPEED_MAX);
    BalloonStateSpaceModel balloonStateSpaceModel = BalloonStateSpaceModels.defaultWithoutUnits();
    StateTime stateTime = new StateTime(Tensors.vector(0, 150, 10, 10), RealScalar.ZERO);
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        balloonStateSpaceModel, EulerIntegrator.INSTANCE, stateTime);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    BalloonEntity balloonEntity = new BalloonEntity(episodeIntegrator, trajectoryControl, balloonStateSpaceModel);
    MouseGoal.simple(owlAnimationFrame, balloonEntity, plannerConstraint);
    Tensor range = Tensors.vector(500, 100).unmodifiable();
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBoundingBox.of( //
        Clips.positive(range.Get(0)), Clips.positive(range.Get(1)));
    Region<Tensor> imageRegion = new BufferedImageRegion( //
        ResourceData.bufferedImage("/io/mountainChain.png"), coordinateBoundingBox, true);
    owlAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    owlAnimationFrame.add(balloonEntity);
    owlAnimationFrame.addBackground(AxesRender.INSTANCE);
    return owlAnimationFrame;
  }

  static void main() {
    new BalloonAnimationDemo().start().jFrame.setVisible(true);
  }
}
