// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.Arrays;

import ch.alpine.java.ren.RenderInterface;
import ch.alpine.java.win.DemoInterface;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2xTEllipsoidStateTimeRegion;
import ch.alpine.owl.bot.rn.glc.R2xTEllipsoidsAnimationDemo;
import ch.alpine.owl.bot.se2.LidarEmulator;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.sim.CameraEmulator;
import ch.alpine.owl.sim.LidarRaytracer;
import ch.alpine.sophus.api.BijectionFamily;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.hs.r2.SimpleR2TranslationFamily;
import ch.alpine.sophus.math.noise.SimplexContinuousNoise;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;

public class Se2xTEllipsoidsDemo implements DemoInterface {
  static final LidarRaytracer LIDAR_RAYTRACER = new LidarRaytracer(Subdivide.of(-1, 1, 32), Subdivide.of(0, 5, 30));

  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    CarxTEntity carxTEntity = new CarxTEntity(new StateTime(Tensors.vector(0, 0, 1), RealScalar.ZERO));
    owlAnimationFrame.add(carxTEntity);
    // ---
    ScalarTensorFunction stf1 = R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(0, 2), 0.05, 2.3);
    BijectionFamily noise1 = new SimpleR2TranslationFamily(stf1);
    Region<StateTime> region1 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.6, 0.8), noise1, () -> carxTEntity.getStateTimeNow().time());
    ScalarTensorFunction stf2 = R2xTEllipsoidsAnimationDemo.wrap1DTensor(SimplexContinuousNoise.FUNCTION, Tensors.vector(1, 3), 0.05, 2.3);
    BijectionFamily noise2 = new SimpleR2TranslationFamily(stf2);
    Region<StateTime> region2 = new R2xTEllipsoidStateTimeRegion( //
        Tensors.vector(0.8, 0.6), noise2, () -> carxTEntity.getStateTimeNow().time());
    TrajectoryRegionQuery trq = new SimpleTrajectoryRegionQuery( //
        RegionUnion.wrap(Arrays.asList(region1, region2)));
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trq);
    MouseGoal.simple(owlAnimationFrame, carxTEntity, plannerConstraint);
    owlAnimationFrame.addBackground((RenderInterface) region1);
    owlAnimationFrame.addBackground((RenderInterface) region2);
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), carxTEntity::getStateTimeNow, trq);
      owlAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, carxTEntity::getStateTimeNow, trq);
      owlAnimationFrame.addBackground(renderInterface);
    }
    // ---
    owlAnimationFrame.geometricComponent.setOffset(350, 350);
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new Se2xTEllipsoidsDemo().start().jFrame.setVisible(true);
  }
}
