// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.ascona.util.ren.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.crv.d2.CogPoints;
import ch.alpine.sophus.hs.r2.Se2Family;
import ch.alpine.sophus.math.api.BijectionFamily;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.sophus.math.noise.SimplexContinuousNoise;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

public class R2xTPolygonAnimationDemo implements DemoInterface {
  private static final Scalar DELAY = RealScalar.of(1.5);

  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(1.2, 2.2), RealScalar.ZERO));
    TrajectoryEntity abstractEntity = new R2xTEntity(episodeIntegrator, DELAY);
    owlAnimationFrame.add(abstractEntity);
    BijectionFamily rigid1 = new Se2Family( //
        scalar -> Tensors.of( //
            Cos.FUNCTION.apply(scalar.multiply(RealScalar.of(0.1))).multiply(RealScalar.of(2.0)), //
            Sin.FUNCTION.apply(scalar.multiply(RealScalar.of(0.1))).multiply(RealScalar.of(2.0)), //
            scalar.multiply(RealScalar.of(0.15))));
    Region<StateTime> region1 = new R2xTPolygonStateTimeRegion( //
        R2ExamplePolygons.CORNER_CENTERED, rigid1, () -> abstractEntity.getStateTimeNow().time());
    BijectionFamily rigid2 = new Se2Family( //
        R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(5, 9, 4), 0.1, 2.0));
    Tensor polygon = CogPoints.of(4, RealScalar.of(1.5), RealScalar.of(0.5));
    Region<StateTime> region2 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid2, () -> abstractEntity.getStateTimeNow().time());
    PlannerConstraint plannerConstraint = //
        RegionConstraints.stateTime(RegionUnion.wrap(region1, region2));
    MouseGoal.simple(owlAnimationFrame, abstractEntity, plannerConstraint);
    owlAnimationFrame.addBackground((RenderInterface) region1);
    owlAnimationFrame.addBackground((RenderInterface) region2);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new R2xTPolygonAnimationDemo().start().jFrame.setVisible(true);
  }
}
