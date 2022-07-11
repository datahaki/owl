// code by jph, gjoel
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.ascona.util.ren.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.se2.LidarEmulator;
import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.adapter.TransitionRegionQueryUnion;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.sim.CameraEmulator;
import ch.alpine.owl.sim.LidarRaytracer;
import ch.alpine.owl.util.ren.MouseShapeRender;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.sca.Clips;

/* package */ class ClothoidRrtsLetterDemo implements DemoInterface {
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-90), Degree.of(90), 32), Subdivide.of(0, 5, 30));

  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.region();
    TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);
    TransitionRegionQuery transitionRegionQuery = TransitionRegionQueryUnion.wrap( //
        new SampledTransitionRegionQuery(region, RealScalar.of(0.05)), //
        new ClothoidCurvatureQuery(Clips.absolute(5.)));
    StateTime stateTime = new StateTime(Tensors.vector(6, 5, Math.PI / 4), RealScalar.ZERO);
    ClothoidRrtsEntity clothoidRrtsEntity = //
        new ClothoidRrtsEntity(stateTime, transitionRegionQuery, r2ImageRegionWrap.coordinateBounds());
    owlAnimationFrame.addBackground(RegionRenders.create(region));
    MouseGoal.simpleRrts(owlAnimationFrame, clothoidRrtsEntity, null);
    owlAnimationFrame.add(clothoidRrtsEntity);
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), clothoidRrtsEntity::getStateTimeNow, trajectoryRegionQuery);
      owlAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, clothoidRrtsEntity::getStateTimeNow, trajectoryRegionQuery);
      owlAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), region)), //
          ClothoidRrtsEntity.SHAPE) {
        @Override
        public Scalar getTime() {
          return clothoidRrtsEntity.getStateTimeNow().time();
        }

        @Override
        public Tensor getSe2() {
          return owlAnimationFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlAnimationFrame.addBackground(renderInterface);
    }
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    owlAnimationFrame.jFrame.setTitle(getClass().getSimpleName());
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new ClothoidRrtsLetterDemo().start().jFrame.setVisible(true);
  }
}
