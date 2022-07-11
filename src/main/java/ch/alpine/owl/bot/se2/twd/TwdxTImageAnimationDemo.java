// code by jph
package ch.alpine.owl.bot.se2.twd;

import ch.alpine.ascona.util.ren.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.r2.R2xTImageStateTimeRegion;
import ch.alpine.owl.bot.se2.LidarEmulator;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.sim.CameraEmulator;
import ch.alpine.owl.sim.LidarRaytracer;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.hs.r2.R2RigidFamily;
import ch.alpine.sophus.hs.r2.Se2Family;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;

/** the obstacle region in the demo is the outside of a rotating letter 'a' */
public class TwdxTImageAnimationDemo extends AbstractTwdDemo {
  static final LidarRaytracer LIDAR_RAYTRACER = new LidarRaytracer(Subdivide.of(-1.2, 1.2, 32), Subdivide.of(0, 4, 25));
  // ---
  private final TwdxTEntity twdxTEntity;
  private final Region<StateTime> region;
  private final TrajectoryRegionQuery trajectoryRegionQuery;

  public TwdxTImageAnimationDemo() {
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(RealScalar.of(1.2), RealScalar.of(0.5));
    twdxTEntity = new TwdxTEntity(twdConfig, new StateTime(Tensors.vector(-1, -1, 1.0), RealScalar.ZERO));
    // ---
    R2RigidFamily rigidFamily = Se2Family.rotationAround( //
        Tensors.vectorDouble(1.5, 2), time -> time.multiply(RealScalar.of(0.1)));
    ImageRegion imageRegion = R2ImageRegions.inside_circ();
    region = new R2xTImageStateTimeRegion( //
        imageRegion, rigidFamily, () -> twdxTEntity.getStateTimeNow().time());
    // ---
    trajectoryRegionQuery = new SimpleTrajectoryRegionQuery(region);
  }

  @Override // from AbstractTwdDemo
  TwdEntity configure(OwlAnimationFrame owlAnimationFrame) {
    owlAnimationFrame.add(twdxTEntity);
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), twdxTEntity::getStateTimeNow, trajectoryRegionQuery);
      owlAnimationFrame.addBackground(renderInterface);
    }
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    MouseGoal.simple(owlAnimationFrame, twdxTEntity, plannerConstraint);
    owlAnimationFrame.addBackground((RenderInterface) region);
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, () -> twdxTEntity.getStateTimeNow(), trajectoryRegionQuery);
      owlAnimationFrame.addBackground(renderInterface);
    }
    owlAnimationFrame.geometricComponent.setOffset(200, 400);
    return twdxTEntity;
  }

  @Override // from AbstractTwdDemo
  Region<StateTime> getRegion() {
    return trajectoryRegionQuery;
  }

  public static void main(String[] args) {
    new TwdxTImageAnimationDemo().start().jFrame.setVisible(true);
  }
}
