// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Collection;

import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.alpine.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.alpine.owl.bot.rn.glc.R2xTEllipsoidsAnimationDemo;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.region.RegionUnion;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophis.crv.d2.ex.CogPoints;
import ch.alpine.sophis.math.Region;
import ch.alpine.sophus.math.bij.BijectionFamily;
import ch.alpine.sophus.math.bij.SimpleR2TranslationFamily;
import ch.alpine.sophus.math.bij.So2Family;
import ch.alpine.sophus.math.noise.SimplexContinuousNoise;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;

public class Rice2dxTParts1Demo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    Scalar mu = RealScalar.of(-.5);
    Collection<Tensor> controls = Rice2Controls.create2d(1).getFlows(15);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    Rice2dEntity abstractEntity = new Rice2dEntity(mu, Tensors.vector(2, 2, 0, 0), trajectoryControl, controls);
    abstractEntity.delayHint = RealScalar.of(1.6);
    owlAnimationFrame.add(abstractEntity);
    ScalarTensorFunction stf1 = R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(4, 2), 0.03, 2.5);
    BijectionFamily noise1 = new SimpleR2TranslationFamily(stf1);
    Region<StateTime> region1 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.4, 0.5), noise1, () -> abstractEntity.getStateTimeNow().time());
    ScalarTensorFunction stf2 = R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(1, 3), 0.03, 2.5);
    BijectionFamily noise2 = new SimpleR2TranslationFamily(stf2);
    Region<StateTime> region2 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.5, 0.6), noise2, () -> abstractEntity.getStateTimeNow().time());
    BijectionFamily rigid2 = new So2Family(RealScalar.of(0.25)::multiply);
    Tensor polygon = CogPoints.of(4, RealScalar.of(1.0), RealScalar.of(0.3));
    Region<StateTime> region3 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid2, () -> abstractEntity.getStateTimeNow().time());
    TrajectoryRegionQuery trq = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(region1, region2, region3));
    // abstractEntity.obstacleQuery = trq;
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trq);
    MouseGoal.simple(owlAnimationFrame, abstractEntity, plannerConstraint);
    // owlyAnimationFrame.addRegionRender(imageRegion);
    owlAnimationFrame.addBackground((RenderInterface) region1);
    owlAnimationFrame.addBackground((RenderInterface) region2);
    owlAnimationFrame.addBackground((RenderInterface) region3);
    owlAnimationFrame.geometricComponent.setOffset(350, 350);
    return owlAnimationFrame;
  }

  static void main() {
    new Rice2dxTParts1Demo().start().jFrame.setVisible(true);
  }
}
