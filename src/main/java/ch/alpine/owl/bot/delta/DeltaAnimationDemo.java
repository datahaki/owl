// code by jph
package ch.alpine.owl.bot.delta;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.alpine.ascony.reg.BufferedImageRegion;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.VectorPlot;
import ch.alpine.bridge.gfx.GeometricComponent;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.PvmBuilder;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlAnimationDemo;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.math.state.EpisodeIntegrator;
import ch.alpine.owlets.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clips;

class DeltaAnimationDemo extends OwlAnimationDemo {
  DeltaAnimationDemo() {
    GeometricComponent geometricComponent = geometricComponent();
    geometricComponent.setRotatable(false);
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
    MouseGoal.simple(geometricComponent, deltaEntity, plannerConstraint);
    add(deltaEntity);
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation);
    geometricComponent.addRenderInterfaceBackground(RegionRenderFactory.create(region));
    geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        Show show = new Show();
        VectorPlot vectorPlot = VectorPlot.of(imageGradientInterpolation::get, cbb);
        show.add(vectorPlot);
        show.render(graphics, geometricLayer.toRectangle(cbb).orElseThrow());
      }
    });
    Tensor fallback_u = Tensors.fromString("{0[s^-1], 0[s^-1]}");
    geometricComponent.addRenderInterfaceBackground( //
        StaticHelper.vectorFieldRender(stateSpaceModel, range, region, fallback_u, Quantity.of(0.8, "s")));
    Tensor digest = PvmBuilder.rhs().setOffset(50, 700).setPerPixel(100).digest();
    geometricComponent.setModel2Pixel(digest);
  }

  static void main() {
    new DeltaAnimationDemo().runStandalone();
  }
}
