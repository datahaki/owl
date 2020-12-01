// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.TransitionRegionQueryUnion;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.sim.CameraEmulator;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.sca.Clips;

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
        new ClothoidRrtsEntity(stateTime, transitionRegionQuery, r2ImageRegionWrap.origin(), r2ImageRegionWrap.range());
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
          ClothoidRrtsEntity.SHAPE, () -> clothoidRrtsEntity.getStateTimeNow().time());
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
