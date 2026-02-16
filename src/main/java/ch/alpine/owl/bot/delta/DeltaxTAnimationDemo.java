// code by jph
package ch.alpine.owl.bot.delta;

import java.util.function.Supplier;

import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.owl.ani.adapter.TemporalTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.ImageGradientInterpolation;
import ch.alpine.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TimeInvariantRegion;
import ch.alpine.owl.region.BufferedImageRegion;
import ch.alpine.owl.region.RegionUnion;
import ch.alpine.owl.util.bot.TrajectoryR2TranslationFamily;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophis.math.api.Region;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.sca.Clips;

public class DeltaxTAnimationDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    Tensor image = Import.of("/io/delta_uxy.png");
    Tensor range = Tensors.vector(12.6, 9.1).unmodifiable(); // overall size of map
    Scalar amp = RealScalar.of(-.05); // direction and strength of river flow
    // ---
    ImageGradientInterpolation imageGradientInterpolation_fast = //
        ImageGradientInterpolation.nearest(image, range, amp);
    TrajectoryControl trajectoryControl = TemporalTrajectoryControl.createInstance();
    StateTime stateTime = new StateTime(Tensors.vector(10, 3.5), RealScalar.ZERO);
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        new DeltaStateSpaceModel(imageGradientInterpolation_fast), EulerIntegrator.INSTANCE, stateTime);
    TrajectoryEntity trajectoryEntity = //
        new DeltaxTEntity(episodeIntegrator, trajectoryControl, imageGradientInterpolation_fast);
    Supplier<Scalar> supplier = () -> trajectoryEntity.getStateTimeNow().time();
    // ---
    ImageGradientInterpolation imageGradientInterpolation_slow = //
        ImageGradientInterpolation.linear(image, range, amp);
    StateSpaceModel stateSpaceModel = new DeltaStateSpaceModel(imageGradientInterpolation_slow);
    Tensor flow = Array.zeros(2);
    Region<StateTime> region1 = create(stateSpaceModel, RealScalar.of(0.4), Tensors.vector(2, 1.5), flow, supplier);
    Region<StateTime> region2 = create(stateSpaceModel, RealScalar.of(0.5), Tensors.vector(6, 6), flow, supplier);
    Region<StateTime> region3 = create(stateSpaceModel, RealScalar.of(0.3), Tensors.vector(2, 7), flow, supplier);
    Region<StateTime> region4 = create(stateSpaceModel, RealScalar.of(0.3), Tensors.vector(1, 8), flow, supplier);
    // ---
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBoundingBox.of( //
        Clips.positive(range.Get(0)), Clips.positive(range.Get(1)));
    MemberQ region = new BufferedImageRegion( //
        ResourceData.bufferedImage("/io/delta_free.png"), coordinateBoundingBox, true);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(new TimeInvariantRegion(region), region1, region2, region3, region4)));
    // ---
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    owlAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlAnimationFrame, trajectoryEntity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenders.create(region));
    owlAnimationFrame.addBackground((RenderInterface) region1);
    owlAnimationFrame.addBackground((RenderInterface) region2);
    owlAnimationFrame.addBackground((RenderInterface) region3);
    owlAnimationFrame.addBackground((RenderInterface) region4);
    owlAnimationFrame.addBackground(StaticHelper.vectorFieldRender(stateSpaceModel, range, region, RealScalar.of(0.5)));
    owlAnimationFrame.geometricComponent.setOffset(50, 600);
    return owlAnimationFrame;
  }

  private static Region<StateTime> create(StateSpaceModel stateSpaceModel, Scalar radius, Tensor pos, Tensor flow, Supplier<Scalar> supplier) {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, stateSpaceModel, Rational.of(1, 10), 120 * 10);
    return new R2xTEllipsoidStateTimeRegion(Tensors.of(radius, radius), //
        TrajectoryR2TranslationFamily.create(stateIntegrator, new StateTime(pos, RealScalar.ZERO), flow), //
        supplier);
  }

  static void main() {
    new DeltaxTAnimationDemo().start().jFrame.setVisible(true);
  }
}
