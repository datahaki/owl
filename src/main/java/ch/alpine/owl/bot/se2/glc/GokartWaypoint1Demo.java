// code by ynager
package ch.alpine.owl.bot.se2.glc;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import ch.alpine.java.ren.WaypointRender;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.bot.r2.WaypointDistanceCost;
import ch.alpine.owl.glc.adapter.EntityGlcPlannerCallback;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.region.ConeRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.lie.se2.Se2Geodesic;
import ch.alpine.sophus.ref.d1.BSpline3CurveSubdivision;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.red.Nest;

/** demo to simulate dubendorf hangar
 * 
 * the cost function rewards path that are proxy to the waypoints
 * therefore a virtual obstacle region in the center to prevent corner cutting is not required. */
public class GokartWaypoint1Demo extends GokartDemo {
  @Override
  protected void configure(OwlAnimationFrame owlAnimationFrame) {
    final StateTime initial = new StateTime(Tensors.vector(33.6, 41.5, 0.6), RealScalar.ZERO);
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180610.csv");
    // System.out.println(Pretty.of(waypoints));
    waypoints = Nest.of(new BSpline3CurveSubdivision(Se2Geodesic.INSTANCE)::cyclic, waypoints, 1);
    CostFunction waypointCost = WaypointDistanceCost.of( //
        waypoints, true, RealScalar.of(1), RealScalar.of(7.5), new Dimension(640, 640));
    GokartVecEntity gokartEntity = new GokartVecEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, Degree.of(18));
      }
    };
    // set cost function hierarchy
    gokartEntity.setCostVector(Arrays.asList(waypointCost), Arrays.asList(0.0));
    gokartEntity.addTimeCost(1, 0.0);
    // ---
    HelperHangarMap hangarMap = new HelperHangarMap("/dubilab/obstacles/20180423.png", gokartEntity);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(hangarMap.region);
    // ---
    owlAnimationFrame.add(gokartEntity);
    owlAnimationFrame.addBackground(RegionRenders.create(hangarMap.imageRegion));
    owlAnimationFrame.geometricComponent.setModel2Pixel(HelperHangarMap.MODEL2PIXEL);
    // ---
    owlAnimationFrame.addBackground(new WaypointRender(ARROWHEAD, COLOR_WAYPOINT).setWaypoints(waypoints));
    GlcPlannerCallback glcPlannerCallback = EntityGlcPlannerCallback.of(gokartEntity);
    GlcWaypointFollowing glcWaypointFollowing = new GlcWaypointFollowing( //
        waypoints, RealScalar.of(2), gokartEntity, plannerConstraint, //
        Arrays.asList(gokartEntity, glcPlannerCallback));
    glcWaypointFollowing.setHorizonDistance(RealScalar.of(8));
    glcWaypointFollowing.startNonBlocking();
    // ---
    owlAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        glcWaypointFollowing.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new GokartWaypoint1Demo().start().jFrame.setVisible(true);
  }
}