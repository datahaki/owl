// code by astoll
package ch.alpine.owl.bot.balloon;

import ch.alpine.ascony.reg.BufferedImageRegion;
import ch.alpine.ascony.ren.AxesRender;
import ch.alpine.bridge.gfx.GeometricComponent;
import ch.alpine.owl.ani.api.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlAnimationDemo;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.math.state.EpisodeIntegrator;
import ch.alpine.owlets.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clips;

class BalloonAnimationDemo extends OwlAnimationDemo {
  BalloonAnimationDemo() {
    GeometricComponent geometricComponent = geometricComponent();
    geometricComponent.setModel2Pixel(Tensors.fromString("{{1.5, 0, 100}, {0, -1.5, 600}, {0, 0, 1}}"));
    PlannerConstraint plannerConstraint = new BalloonPlannerConstraint(BalloonEntity.SPEED_MAX);
    BalloonStateSpaceModel balloonStateSpaceModel = BalloonStateSpaceModels.defaultWithUnits();
    StateTime stateTime = new StateTime(Tensors.vector(0, 150, 10, 10), Quantity.of(0, "s^-1"));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        balloonStateSpaceModel, TimeIntegrators.EULER, stateTime);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    BalloonEntity balloonEntity = new BalloonEntity(episodeIntegrator, trajectoryControl, balloonStateSpaceModel);
    MouseGoal.simple(geometricComponent, balloonEntity, plannerConstraint);
    Tensor range = Tensors.vector(500, 100).unmodifiable();
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBoundingBox.of( //
        Clips.positive(range.Get(0)), Clips.positive(range.Get(1)));
    MemberQ imageRegion = new BufferedImageRegion( //
        ResourceData.bufferedImage("/io/mountainChain.png"), coordinateBoundingBox, true);
    geometricComponent.addRenderInterfaceBackground(RegionRenderFactory.create(imageRegion));
    add(balloonEntity);
    geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    IO.println(Pretty.of(geometricComponent.getModel2Pixel()));
  }

  static void main() {
    new BalloonAnimationDemo().runStandalone();
  }
}
