// code by ynager
package ch.alpine.owl.bot.se2.glc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.GlcPlannerCallback;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.EntityGlcPlannerCallback;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.ren.WaypointRender;
import ch.alpine.owl.math.region.ConeRegion;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.sophus.lie.se2.Se2Geodesic;
import ch.alpine.sophus.math.Region;
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
  protected void configure(OwlAnimationFrame owlyAnimationFrame) {
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
    waypoints = new BSpline2CurveSubdivision(Se2Geodesic.INSTANCE).cyclic(waypoints);
    Region<Tensor> polygonRegion = new PolygonRegion(VIRTUAL);
    Region<Tensor> union = RegionUnion.wrap(Arrays.asList(hangarMap.region, polygonRegion));
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(union);
    // ---
    owlyAnimationFrame.add(gokartEntity);
    owlyAnimationFrame.addBackground(RegionRenders.create(hangarMap.imageRegion));
    owlyAnimationFrame.addBackground(RegionRenders.create(polygonRegion));
    owlyAnimationFrame.geometricComponent.setModel2Pixel(HelperHangarMap.MODEL2PIXEL);
    // ---
    owlyAnimationFrame.addBackground(new WaypointRender(ARROWHEAD, COLOR_WAYPOINT).setWaypoints(waypoints));
    GlcPlannerCallback glcPlannerCallback = EntityGlcPlannerCallback.of(gokartEntity);
    GlcWaypointFollowing glcWaypointFollowing = new GlcWaypointFollowing( //
        waypoints, RealScalar.of(2), gokartEntity, plannerConstraint, //
        Arrays.asList(gokartEntity, glcPlannerCallback));
    glcWaypointFollowing.setHorizonDistance(RealScalar.of(7));
    glcWaypointFollowing.startNonBlocking();
    // ---
    owlyAnimationFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        glcWaypointFollowing.flagShutdown();
      }
    });
  }

  public static void main(String[] args) {
    new GokartWaypoint0Demo().start().jFrame.setVisible(true);
  }
}