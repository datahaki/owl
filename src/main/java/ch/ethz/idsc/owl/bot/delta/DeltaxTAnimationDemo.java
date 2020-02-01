// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.Arrays;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.ani.adapter.TemporalTrajectoryControl;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.bot.util.TrajectoryR2TranslationFamily;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModels;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.RegionUnion;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TimeInvariantRegion;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.ResourceData;

public class DeltaxTAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    Tensor image = ResourceData.of("/io/delta_uxy.png");
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
    Flow flow = StateSpaceModels.createFlow(stateSpaceModel, Array.zeros(2));
    Region<StateTime> region1 = create(stateSpaceModel, RealScalar.of(0.4), Tensors.vector(2, 1.5), flow, supplier);
    Region<StateTime> region2 = create(stateSpaceModel, RealScalar.of(0.5), Tensors.vector(6, 6), flow, supplier);
    Region<StateTime> region3 = create(stateSpaceModel, RealScalar.of(0.3), Tensors.vector(2, 7), flow, supplier);
    Region<StateTime> region4 = create(stateSpaceModel, RealScalar.of(0.3), Tensors.vector(1, 8), flow, supplier);
    // ---
    Region<Tensor> region = ImageRegion.of(ResourceData.bufferedImage("/io/delta_free.png"), range, true);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList(new TimeInvariantRegion(region), region1, region2, region3, region4))));
    // ---
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlyAnimationFrame, trajectoryEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    owlyAnimationFrame.addBackground((RenderInterface) region1);
    owlyAnimationFrame.addBackground((RenderInterface) region2);
    owlyAnimationFrame.addBackground((RenderInterface) region3);
    owlyAnimationFrame.addBackground((RenderInterface) region4);
    owlyAnimationFrame.addBackground(DeltaHelper.vectorFieldRender(stateSpaceModel, range, region, RealScalar.of(0.5)));
    owlyAnimationFrame.configCoordinateOffset(50, 600);
    return owlyAnimationFrame;
  }

  private static Region<StateTime> create(StateSpaceModel stateSpaceModel, Scalar radius, Tensor pos, Flow flow, Supplier<Scalar> supplier) {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, stateSpaceModel, RationalScalar.of(1, 10), 120 * 10);
    return new R2xTEllipsoidStateTimeRegion(Tensors.of(radius, radius), //
        TrajectoryR2TranslationFamily.create(stateIntegrator, new StateTime(pos, RealScalar.ZERO), flow), //
        supplier);
  }

  public static void main(String[] args) throws Exception {
    new DeltaxTAnimationDemo().start().jFrame.setVisible(true);
  }
}
