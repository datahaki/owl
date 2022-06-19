// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.alpine.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.alpine.owl.bot.se2.LidarEmulator;
import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.bot.se2.rl.CarPolicyEntity;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TimeInvariantRegion;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.sim.CameraEmulator;
import ch.alpine.owl.sim.LidarRaytracer;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.crv.d2.CogPoints;
import ch.alpine.sophus.hs.r2.Se2Family;
import ch.alpine.sophus.hs.r2.SimpleR2TranslationFamily;
import ch.alpine.sophus.math.api.BijectionFamily;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.subare.core.td.SarsaType;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;

public class Se2xTLetterDemo implements DemoInterface {
  static final LidarRaytracer LIDAR_RAYTRACER = new LidarRaytracer(Subdivide.of(-1, 1, 32), Subdivide.of(0, 5, 20));

  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    CarxTEntity carxTEntity = new CarxTEntity(new StateTime(Tensors.vector(6.75, 5.4, 1 + Math.PI), RealScalar.ZERO));
    owlAnimationFrame.add(carxTEntity);
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.region();
    carxTEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    // ---
    BijectionFamily oscillation = new SimpleR2TranslationFamily(s -> Tensors.vector( //
        Math.sin(s.number().doubleValue() * .12) * 3.0 + 3.6, 4.0));
    Region<StateTime> region1 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.4, 0.5), oscillation, () -> carxTEntity.getStateTimeNow().time());
    // ---
    BijectionFamily rigid3 = new Se2Family(s -> Tensors.vector(8.0, 5.8, s.number().doubleValue() * 0.5));
    Tensor polygon = CogPoints.of(3, RealScalar.of(1.0), RealScalar.of(0.3));
    Region<StateTime> cog0 = new R2xTPolygonStateTimeRegion( //
        polygon, rigid3, () -> carxTEntity.getStateTimeNow().time());
    // ---
    Region<Tensor> se2PointsVsRegion = Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), region);
    TrajectoryRegionQuery trajectoryRegionQuery = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap( //
            new TimeInvariantRegion(se2PointsVsRegion), // <- expects se2 states
            region1, cog0 //
        ));
    // Se2PointsVsRegion se2PointsVsRegion = Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), RegionUnion.wrap(Arrays.asList( //
    // new TimeInvariantRegion(imageRegion), // <- expects se2 states
    // region1, cog0 //
    // )));
    // TrajectoryRegionQuery trq = new SimpleTrajectoryRegionQuery( //
    // );
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    // abstractEntity.raytraceQuery = SimpleTrajectoryRegionQuery.timeInvariant(imageRegion);
    MouseGoal.simple(owlAnimationFrame, carxTEntity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenders.create(region));
    owlAnimationFrame.addBackground((RenderInterface) region1);
    // owlyAnimationFrame.addBackground((RenderInterface) region2);
    owlAnimationFrame.addBackground((RenderInterface) cog0);
    // ---
    final TrajectoryRegionQuery ray = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap( //
            new TimeInvariantRegion(region), //
            region1,
            // region2,
            cog0 //
        ));
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), carxTEntity::getStateTimeNow, ray);
      owlAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, carxTEntity::getStateTimeNow, ray);
      owlAnimationFrame.addBackground(renderInterface);
    }
    {
      CarPolicyEntity twdPolicyEntity = new CarPolicyEntity( //
          Tensors.vector(5.600, 8.667, -1.571), SarsaType.QLEARNING, ray);
      owlAnimationFrame.add(twdPolicyEntity);
    }
    // ---
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new Se2xTLetterDemo().start().jFrame.setVisible(true);
  }
}
