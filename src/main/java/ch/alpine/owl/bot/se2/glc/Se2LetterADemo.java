// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.se2.LidarEmulator;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.ren.MouseShapeRender;
import ch.alpine.owl.gui.win.MouseGoal;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.region.ConeRegion;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.sim.CameraEmulator;
import ch.alpine.owl.sim.LidarRaytracer;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.qty.Degree;

public abstract class Se2LetterADemo extends Se2CarDemo {
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-90), Degree.of(90), 32), Subdivide.of(0, 5, 30));

  @Override // from Se2CarDemo
  protected final void configure(OwlyAnimationFrame owlyAnimationFrame) {
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    StateTime stateTime = new StateTime(Tensors.vector(6, 5, 1), RealScalar.ZERO);
    CarEntity carEntity = new CarEntity( //
        stateTime, //
        createTrajectoryControl(), //
        CarEntity.PARTITION_SCALE, CarEntity.CARFLOWS, CarEntity.SHAPE) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, Degree.of(30));
      }
    };
    carEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    Region<Tensor> region = r2ImageRegionWrap.region();
    PlannerConstraint plannerConstraint = createConstraint(region);
    TrajectoryRegionQuery trajectoryRegionQuery = //
        SimpleTrajectoryRegionQuery.timeInvariant(region);
    owlyAnimationFrame.add(carEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    MouseGoal.simple(owlyAnimationFrame, carEntity, plannerConstraint);
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), carEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, carEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(region)), //
          CarEntity.SHAPE, () -> carEntity.getStateTimeNow().time());
      owlyAnimationFrame.addBackground(renderInterface);
    }
  }

  public abstract TrajectoryControl createTrajectoryControl();
}
