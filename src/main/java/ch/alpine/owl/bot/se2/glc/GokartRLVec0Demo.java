// code by astoll
package ch.alpine.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.ascona.util.ren.RenderInterface;
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
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.ren.MouseShapeRender;
import ch.alpine.owl.util.ren.PolygonRegionRender;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class GokartRLVec0Demo extends GokartDemo {
  private static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });

  @Override
  protected void configure(OwlAnimationFrame owlAnimationFrame) {
    // initial state time
    final StateTime initial = new StateTime(Tensors.vector(0, 10, 0), RealScalar.ZERO);
    // goal
    Tensor goal = Tensors.vector(25, 10, 0);
    // slacks
    Tensor slacks = Tensors.vector(0.5, 0);
    // polygon region as cost function
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 3, 10 }, { 3, 0 }, { 10, 0 }, { 10, 15 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint regionConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCosts = ConstraintViolationCost.of(regionConstraint, RealScalar.ONE);
    // set up relaxed gokart entity
    GokartRelaxedEntity gokartEntity = GokartRelaxedEntity.createRelaxedGokartEntity(initial, slacks);
    gokartEntity.setAdditionalCostFunction(regionCosts);
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
    goalconsumer.accept(goal);
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
    new GokartRLVec0Demo().start().jFrame.setVisible(true);
  }
}
