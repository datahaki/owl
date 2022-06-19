// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.se2.LidarEmulator;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.region.ConeRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.sim.CameraEmulator;
import ch.alpine.owl.sim.LidarRaytracer;
import ch.alpine.owl.util.ren.MouseShapeRender;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.qty.Degree;

public abstract class Se2LetterADemo extends Se2CarDemo {
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-90), Degree.of(90), 32), Subdivide.of(0, 5, 30));

  @Override // from Se2CarDemo
  protected final void configure(OwlAnimationFrame owlAnimationFrame) {
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
    owlAnimationFrame.add(carEntity);
    owlAnimationFrame.addBackground(RegionRenders.create(region));
    MouseGoal.simple(owlAnimationFrame, carEntity, plannerConstraint);
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), carEntity::getStateTimeNow, trajectoryRegionQuery);
      owlAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, carEntity::getStateTimeNow, trajectoryRegionQuery);
      owlAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(region)), //
          CarEntity.SHAPE) {
        @Override
        public Scalar getTime() {
          return carEntity.getStateTimeNow().time();
        }

        @Override
        public Tensor getSe2() {
          return owlAnimationFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlAnimationFrame.addBackground(renderInterface);
    }
  }

  public abstract TrajectoryControl createTrajectoryControl();
}
