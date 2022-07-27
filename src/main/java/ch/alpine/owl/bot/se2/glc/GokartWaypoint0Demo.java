// code by ynager
package ch.alpine.owl.bot.se2.glc;

import java.util.List;

import ch.alpine.bridge.awt.WindowClosed;
import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.glc.adapter.EntityGlcPlannerCallback;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.region.ConeRegion;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.sophus.ref.d1.BSpline2CurveSubdivision;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.qty.Degree;

/** demo to simulate dubendorf hangar
 * 
 * a virtual obstacle is added in the center to prevent the gokart from corner cutting */
public class GokartWaypoint0Demo extends GokartDemo {
  private static final Tensor VIRTUAL = Tensors.fromString("{{38, 39}, {42, 47}, {51, 52}, {46, 43}}");

  @Override
  protected void configure(OwlAnimationFrame owlAnimationFrame) {
    final StateTime initial = new StateTime(Tensors.vector(33.6, 41.5, 0.6), RealScalar.ZERO);
    GokartEntity gokartEntity = new GokartEntity(initial) {
      @Override
      public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
        return new ConeRegion(goal, Degree.of(18));
      }
    };
    // ---
    HelperHangarMap hangarMap = new HelperHangarMap("/dubilab/obstacles/20180423.png", gokartEntity);
    // ---
    Tensor waypoints = ResourceData.of("/dubilab/waypoints/20180425.csv");
    waypoints = new BSpline2CurveSubdivision(Se2Group.INSTANCE).cyclic(waypoints);
    Region<Tensor> polygonRegion = new PolygonRegion(VIRTUAL);
    Region<Tensor> union = RegionUnion.wrap(hangarMap.region, polygonRegion);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(union);
    // ---
    owlAnimationFrame.add(gokartEntity);
    owlAnimationFrame.addBackground(RegionRenders.create(hangarMap.imageRegion));
    owlAnimationFrame.addBackground(RegionRenders.create(polygonRegion));
    owlAnimationFrame.geometricComponent.setModel2Pixel(HelperHangarMap.MODEL2PIXEL);
    // ---
    owlAnimationFrame.addBackground(new WaypointRender(ARROWHEAD, COLOR_WAYPOINT).setWaypoints(waypoints));
    GlcPlannerCallback glcPlannerCallback = EntityGlcPlannerCallback.of(gokartEntity);
    GlcWaypointFollowing glcWaypointFollowing = new GlcWaypointFollowing( //
        waypoints, RealScalar.of(2), gokartEntity, plannerConstraint, //
        List.of(gokartEntity, glcPlannerCallback));
    glcWaypointFollowing.setHorizonDistance(RealScalar.of(7));
    glcWaypointFollowing.startNonBlocking();
    // ---
    WindowClosed.runs(owlAnimationFrame.jFrame, () -> glcWaypointFollowing.flagShutdown());
  }

  public static void main(String[] args) {
    new GokartWaypoint0Demo().start().jFrame.setVisible(true);
  }
}
