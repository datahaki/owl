// code by ynager
package ch.alpine.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.alpine.java.ren.RenderInterface;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.glc.adapter.ConstraintViolationCost;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EntityGlcPlannerCallback;
import ch.alpine.owl.glc.adapter.GoalConsumer;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.adapter.SimpleGoalConsumer;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.ren.MouseShapeRender;
import ch.alpine.owl.gui.ren.PolygonRegionRender;
import ch.alpine.owl.math.region.ConeRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Degree;

/** demo to show effect of lexicographic vector cost comparison
 * 1. Cost: Time
 * 2. Cost: Polygon region penalty */
public class GokartVec0Demo extends GokartDemo {
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });

  @Override
  protected void configure(OwlAnimationFrame owlAnimationFrame) {
    final StateTime initial = new StateTime(Tensors.vector(0, 10, 0), RealScalar.ZERO);
    GokartVecEntity gokartEntity = new GokartVecEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, Degree.of(18));
      }
    };
    // define cost function hierarchy
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 3, 10 }, { 3, 0 }, { 23, 0 }, { 23, 20 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint regionConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(regionConstraint, RealScalar.ONE);
    gokartEntity.setCostVector(Arrays.asList(regionCost), Arrays.asList(0.0));
    gokartEntity.addTimeCost(0, 0.8); // set priority to 0, allow for 0.8 seconds of slack
    // ---
    PlannerConstraint plannerConstraint = EmptyPlannerConstraint.INSTANCE;
    // ---
    owlAnimationFrame.add(gokartEntity);
    owlAnimationFrame.geometricComponent.setModel2Pixel(MODEL2PIXEL);
    owlAnimationFrame.addBackground(new PolygonRegionRender(polygonRegion));
    // ---
    List<GlcPlannerCallback> list = new ArrayList<>();
    list.add(gokartEntity);
    list.add(EntityGlcPlannerCallback.of(gokartEntity));
    GoalConsumer goalconsumer = new SimpleGoalConsumer(gokartEntity, plannerConstraint, list);
    // try {
    // TimeUnit.SECONDS.sleep(1);
    // } catch (InterruptedException e) {
    // // ---
    // }
    goalconsumer.accept(Tensors.vector(35, 10, 0));
    MouseGoal.simple(owlAnimationFrame, gokartEntity, plannerConstraint);
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(polygonRegion), //
          CarEntity.SHAPE) {
        @Override
        public Scalar getTime() {
          return gokartEntity.getStateTimeNow().time();
        }

        @Override
        public Tensor getSe2() {
          return owlAnimationFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlAnimationFrame.addBackground(renderInterface);
    }
  }

  public static void main(String[] args) {
    new GokartVec0Demo().start().jFrame.setVisible(true);
  }
}