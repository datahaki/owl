// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.util.Arrays;

import ch.alpine.java.ren.RenderInterface;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.hs.r2.Se2Family;
import ch.alpine.sophus.hs.r2.SimpleR2TranslationFamily;
import ch.alpine.sophus.math.BijectionFamily;
import ch.alpine.sophus.math.Region;
import ch.alpine.sophus.math.noise.NativeContinuousNoise;
import ch.alpine.sophus.math.noise.SimplexContinuousNoise;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.lie.r2.AngleVector;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.Sin;

public class R2xTEllipsoidsAnimationDemo implements DemoInterface {
  private static final Scalar DELAY = RealScalar.of(1.2);

  public static ScalarTensorFunction wrap1DTensor(NativeContinuousNoise nativeContinuousNoise, Tensor offset, double period, double amplitude) {
    return scalar -> Tensor.of(offset.stream().map(Scalar.class::cast)
        .map(value -> RealScalar.of(amplitude * nativeContinuousNoise.at(scalar.number().doubleValue() * period, value.number().doubleValue()))));
  }

  @SuppressWarnings("unused")
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(1.2, 2), RealScalar.ZERO));
    TrajectoryEntity abstractEntity = new R2xTEntity(episodeIntegrator, DELAY);
    owlAnimationFrame.add(abstractEntity);
    BijectionFamily shiftx = new SimpleR2TranslationFamily( //
        scalar -> Tensors.of(Sin.FUNCTION.apply(scalar.multiply(RealScalar.of(0.2))), RealScalar.ZERO));
    BijectionFamily shifty = new SimpleR2TranslationFamily( //
        scalar -> Tensors.of(RealScalar.ZERO, //
            Cos.FUNCTION.apply(scalar.multiply(RealScalar.of(0.27)).multiply(RealScalar.of(2)))));
    BijectionFamily circle = new SimpleR2TranslationFamily( //
        scalar -> AngleVector.of(scalar.multiply(RealScalar.of(0.2))).multiply(RealScalar.of(2)));
    BijectionFamily noise = new SimpleR2TranslationFamily( //
        R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(0, 2), 0.1, 1.3));
    BijectionFamily rigidm = new Se2Family( //
        R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(5, 9, 4), 0.1, 2.0));
    Region<StateTime> region1 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.7, 0.9), circle, () -> abstractEntity.getStateTimeNow().time());
    Region<StateTime> region2 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.8, 0.5), rigidm, () -> abstractEntity.getStateTimeNow().time());
    Region<StateTime> region3 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.6, 0.6), noise, () -> abstractEntity.getStateTimeNow().time());
    Region<StateTime> union = RegionUnion.wrap(Arrays.asList(region1, region2, region3));
    PlannerConstraint plannerConstraint = RegionConstraints.stateTime(union);
    MouseGoal.simple(owlAnimationFrame, abstractEntity, plannerConstraint);
    owlAnimationFrame.addBackground((RenderInterface) region1);
    owlAnimationFrame.addBackground((RenderInterface) region2);
    owlAnimationFrame.addBackground((RenderInterface) region3);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new R2xTEllipsoidsAnimationDemo().start().jFrame.setVisible(true);
  }
}
