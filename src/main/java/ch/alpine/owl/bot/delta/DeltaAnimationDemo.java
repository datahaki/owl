// code by jph
package ch.alpine.owl.bot.delta;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.alpine.ascony.reg.BufferedImageRegion;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.VectorPlot;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.math.state.EpisodeIntegrator;
import ch.alpine.owlets.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clips;

public class DeltaAnimationDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame getTimerFrame() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    // ---
    Scalar amp = Quantity.of(-0.05, "s^-1");
    Tensor range = Tensors.vector(12.6, 9.1).unmodifiable();
    ImageGradientInterpolation imageGradientInterpolation = //
        ImageGradientInterpolation.nearest(Import.of("/io/delta_uxy.png"), range, amp);
    BufferedImage bufferedImage = ResourceData.bufferedImage("/io/delta_free.png");
    CoordinateBoundingBox cbb = CoordinateBoundingBox.of( //
        Clips.positive(range.Get(0)), Clips.positive(range.Get(1)));
    MemberQ region = new BufferedImageRegion(bufferedImage, cbb, true);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    StateTime stateTime = new StateTime(Tensors.vector(10, 3.5), Quantity.of(0, "s"));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        new DeltaStateSpaceModel(imageGradientInterpolation), TimeIntegrators.EULER, stateTime);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    DeltaEntity deltaEntity = new DeltaEntity(episodeIntegrator, trajectoryControl, imageGradientInterpolation);
    MouseGoal.simple(owlAnimationFrame, deltaEntity, plannerConstraint);
    owlAnimationFrame.add(deltaEntity);
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    owlAnimationFrame.addBackground(RegionRenderFactory.create(region));
    owlAnimationFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        Show show = new Show();
        VectorPlot vectorPlot = VectorPlot.of(imageGradientInterpolation::get, cbb);
        show.add(vectorPlot);
        show.render(graphics, geometricLayer.toRectangle(cbb));
      }
    });
    // owlAnimationFrame.addBackground(StaticHelper.vectorFieldRender(stateSpaceModel, range, region, RealScalar.of(0.8)));
    owlAnimationFrame.geometricComponent.setOffset(50, 600);
    owlAnimationFrame.geometricComponent.setPerPixel(RealScalar.of(60));
    return owlAnimationFrame;
  }

  static void main() {
    new DeltaAnimationDemo().runStandalone();
  }
}
