// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Arrays;
import java.util.Collection;

import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.alpine.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.alpine.owl.bot.rn.glc.R2xTEllipsoidsAnimationDemo;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.win.MouseGoal;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.noise.SimplexContinuousNoise;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophus.hs.r2.SimpleR2TranslationFamily;
import ch.alpine.sophus.hs.r2.So2Family;
import ch.alpine.sophus.math.BijectionFamily;
import ch.alpine.sophus.ply.CogPoints;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarTensorFunction;

public class Rice2dxTParts1Demo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Scalar mu = RealScalar.of(-.5);
    Collection<Tensor> controls = Rice2Controls.create2d(1).getFlows(15);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    Rice2dEntity abstractEntity = new Rice2dEntity(mu, Tensors.vector(2, 2, 0, 0), trajectoryControl, controls);
    abstractEntity.delayHint = RealScalar.of(1.6);
    owlyAnimationFrame.add(abstractEntity);
    ScalarTensorFunction stf1 = R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(4, 2), 0.03, 2.5);
    BijectionFamily noise1 = new SimpleR2TranslationFamily(stf1);
    Region<StateTime> region1 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.4, 0.5), noise1, () -> abstractEntity.getStateTimeNow().time());
    ScalarTensorFunction stf2 = R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(1, 3), 0.03, 2.5);
    BijectionFamily noise2 = new SimpleR2TranslationFamily(stf2);
    Region<StateTime> region2 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.5, 0.6), noise2, () -> abstractEntity.getStateTimeNow().time());
    BijectionFamily rigid2 = new So2Family(s -> s.multiply(RealScalar.of(0.25)));
    Tensor polygon = CogPoints.of(4, RealScalar.of(1.0), RealScalar.of(0.3));
    Region<StateTime> region3 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid2, () -> abstractEntity.getStateTimeNow().time());
    TrajectoryRegionQuery trq = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList(region1, region2, region3)));
    // abstractEntity.obstacleQuery = trq;
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trq);
    MouseGoal.simple(owlyAnimationFrame, abstractEntity, plannerConstraint);
    // owlyAnimationFrame.addRegionRender(imageRegion);
    owlyAnimationFrame.addBackground((RenderInterface) region1);
    owlyAnimationFrame.addBackground((RenderInterface) region2);
    owlyAnimationFrame.addBackground((RenderInterface) region3);
    owlyAnimationFrame.geometricComponent.setOffset(350, 350);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice2dxTParts1Demo().start().jFrame.setVisible(true);
  }
}
