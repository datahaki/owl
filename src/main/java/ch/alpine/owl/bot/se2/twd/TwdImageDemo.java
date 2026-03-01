// code by jph
package ch.alpine.owl.bot.se2.twd;

import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.se2.LidarEmulator;
import ch.alpine.owl.sim.CameraEmulator;
import ch.alpine.owl.sim.LidarRaytracer;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TrajectoryRegionQuery;
import ch.alpine.sophis.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.chq.MemberQ;

public class TwdImageDemo extends AbstractTwdDemo {
  static final LidarRaytracer LIDAR_RAYTRACER = new LidarRaytracer(Subdivide.of(-1, 1, 26), Subdivide.of(0, 4, 30));
  // ---
  private final R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._0F5C_2182;
  private final MemberQ region = r2ImageRegionWrap.region();
  private final TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);

  @Override // from AbstractTwdDemo
  TwdEntity configure(OwlAnimationFrame owlAnimationFrame) {
    TwdEntity twdEntity = TwdEntity.createJ2B2(new StateTime(Tensors.vector(7, 5, 0), RealScalar.ZERO));
    twdEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    owlAnimationFrame.add(twdEntity);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    MouseGoal.simple(owlAnimationFrame, twdEntity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenderFactory.create(region));
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), twdEntity::getStateTimeNow, trajectoryRegionQuery);
      owlAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, twdEntity::getStateTimeNow, trajectoryRegionQuery);
      owlAnimationFrame.addBackground(renderInterface);
    }
    return twdEntity;
  }

  @Override // from AbstractTwdDemo
  Region<StateTime> getRegion() {
    return trajectoryRegionQuery;
  }

  static void main() {
    new TwdImageDemo().runStandalone();
  }
}
