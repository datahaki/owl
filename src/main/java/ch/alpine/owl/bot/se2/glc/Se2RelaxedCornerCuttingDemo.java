// code by astoll
package ch.alpine.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.se2.LidarEmulator;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.EntityGlcPlannerCallback;
import ch.alpine.owl.glc.adapter.GoalConsumer;
import ch.alpine.owl.glc.adapter.SimpleGoalConsumer;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.ren.MouseShapeRender;
import ch.alpine.owl.gui.win.MouseGoal;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.sim.CameraEmulator;
import ch.alpine.owl.sim.LidarRaytracer;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.qty.Degree;

public class Se2RelaxedCornerCuttingDemo extends Se2CarDemo {
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-90), Degree.of(90), 32), Subdivide.of(0, 5, 30));

  static R2ImageRegionWrap createResLo() {
    BufferedImage bufferedImage = new BufferedImage(64, 64, BufferedImage.TYPE_BYTE_GRAY);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 64, 64);
    graphics.setColor(Color.BLACK);
    graphics.fillRect(50, 7, 7, 20);
    graphics.fillRect(27, 20, 23, 7);
    graphics.fillRect(27, 27, 7, 20);
    graphics.fillRect(7, 40, 20, 7);
    Tensor image = Transpose.of(ImageFormat.from(bufferedImage));
    Tensor range = Tensors.vector(12, 12);
    int ttl = 2;
    return new R2ImageRegionWrap(ImageFormat.of(image), range, ttl);
  }

  @Override // from Se2CarDemo
  protected final void configure(OwlyAnimationFrame owlyAnimationFrame) {
    StateTime stateTime = new StateTime(Tensors.vector(1.7, 2.2, 0), RealScalar.ZERO);
    Tensor slacks = Tensors.vector(1.5, 0);
    CarRelaxedEntity carRelaxedEntity = CarRelaxedEntity.createDefault(stateTime, slacks);
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = createResLo();
    carRelaxedEntity.setAdditionalCostFunction(r2ImageRegionWrap.costFunction());
    // ---
    Region<Tensor> region = r2ImageRegionWrap.region();
    PlannerConstraint plannerConstraint = createConstraint(region);
    TrajectoryRegionQuery trajectoryRegionQuery = //
        SimpleTrajectoryRegionQuery.timeInvariant(region);
    // owlyAnimationFrame.addBackground(RegionRenders.create(testImageRegion));
    List<GlcPlannerCallback> list = new ArrayList<>();
    list.add(carRelaxedEntity);
    list.add(EntityGlcPlannerCallback.of(carRelaxedEntity));
    // ---
    GoalConsumer goalConsumer = new SimpleGoalConsumer(carRelaxedEntity, plannerConstraint, list);
    Tensor goal = Tensors.vector(4.3, 4.2, 1.517);
    goalConsumer.accept(goal);
    // ---
    owlyAnimationFrame.add(carRelaxedEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    MouseGoal.simple(owlyAnimationFrame, carRelaxedEntity, plannerConstraint);
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), carRelaxedEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, carRelaxedEntity::getStateTimeNow, trajectoryRegionQuery);
      owlyAnimationFrame.addBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(region)), //
          CarEntity.SHAPE) {
        @Override
        public Scalar getTime() {
          return carRelaxedEntity.getStateTimeNow().time();
        }

        @Override
        public Tensor getSe2() {
          return owlyAnimationFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlyAnimationFrame.addBackground(renderInterface);
    }
  }

  public static void main(String[] args) {
    new Se2RelaxedCornerCuttingDemo().start().jFrame.setVisible(true);
  }
}
