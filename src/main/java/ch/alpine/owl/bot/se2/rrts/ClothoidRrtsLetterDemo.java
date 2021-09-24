// code by jph, gjoel
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.se2.LidarEmulator;
import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.ren.MouseShapeRender;
import ch.alpine.owl.gui.win.MouseGoal;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.adapter.TransitionRegionQueryUnion;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.sim.CameraEmulator;
import ch.alpine.owl.sim.LidarRaytracer;
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
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.region();
    TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);
    TransitionRegionQuery transitionRegionQuery = TransitionRegionQueryUnion.wrap( //
        new SampledTransitionRegionQuery(region, RealScalar.of(0.05)), //
        new ClothoidCurvatureQuery(Clips.absolute(5.)));
    StateTime stateTime = new StateTime(Tensors.vector(6, 5, Math.PI / 4), RealScalar.ZERO);
    ClothoidRrtsEntity clothoidRrtsEntity = //
        new ClothoidRrtsEntity(stateTime, transitionRegionQuery, r2ImageRegionWrap.ndBox());
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    MouseGoal.simpleRrts(owlyAnimationFrame, clothoidRrtsEntity, null);
    owlyAnimationFrame.add(clothoidRrtsEntity);
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), clothoidRrtsEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, clothoidRrtsEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
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
          return owlyAnimationFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlyAnimationFrame.addBackground(renderInterface);
    }
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    owlyAnimationFrame.jFrame.setTitle(getClass().getSimpleName());
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new ClothoidRrtsLetterDemo().start().jFrame.setVisible(true);
  }
}
