// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Arrays;
import java.util.Collection;

import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.java.win.DemoInterface;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophus.api.BijectionFamily;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.crv.d2.CogPoints;
import ch.alpine.sophus.hs.r2.Se2Family;
import ch.alpine.sophus.hs.r2.So2Family;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Rice2dxTGearDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    Scalar mu = RealScalar.of(-.5);
    Collection<Tensor> controls = Rice2Controls.create2d(1).getFlows(15);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    Rice2dEntity abstractEntity = new Rice2dEntity(mu, Tensors.vector(2, 2, 0, 0), trajectoryControl, controls);
    abstractEntity.delayHint = RealScalar.of(1.6);
    owlAnimationFrame.add(abstractEntity);
    Tensor polygon = CogPoints.of(4, RealScalar.of(1.0), RealScalar.of(0.3));
    BijectionFamily rigid2 = new So2Family(s -> s.multiply(RealScalar.of(0.25)));
    Region<StateTime> cog0 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid2, () -> abstractEntity.getStateTimeNow().time());
    BijectionFamily rigid3 = new Se2Family(s -> Tensors.of( //
        RealScalar.of(1.7), RealScalar.ZERO, RealScalar.of(-1.2).add(s.multiply(RealScalar.of(-.25)))));
    Region<StateTime> cog1 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid3, () -> abstractEntity.getStateTimeNow().time());
    BijectionFamily rigid1 = new Se2Family(s -> Tensors.of( //
        RealScalar.of(1.7 * 2), RealScalar.ZERO, s.multiply(RealScalar.of(0.25))));
    Region<StateTime> cog2 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid1, () -> abstractEntity.getStateTimeNow().time());
    TrajectoryRegionQuery trq = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList(cog0, cog1, cog2)));
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trq);
    MouseGoal.simple(owlAnimationFrame, abstractEntity, plannerConstraint);
    owlAnimationFrame.addBackground((RenderInterface) cog0);
    owlAnimationFrame.addBackground((RenderInterface) cog1);
    owlAnimationFrame.addBackground((RenderInterface) cog2);
    owlAnimationFrame.geometricComponent.setOffset(350, 350);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice2dxTGearDemo().start().jFrame.setVisible(true);
  }
}
