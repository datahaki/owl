// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.bot.se2.Se2CarIntegrator;
import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.bot.se2.Se2CoveringWrap;
import ch.alpine.owl.bot.se2.Se2MinTimeGoalManager;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.bot.se2.Se2Wrap;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.CoordinateWrap;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.region.So2Region;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeTensorFunction;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Degree;

/** Notice:
 * 
 * CLASS Se2WrapDemo IS USED IN TESTS
 * MODIFICATION MAY INVALIDATE THE TESTS
 * 
 * (x, y, theta) */
enum Se2WrapDemo {
  ;
  static TrajectoryRegionQuery obstacleQuery() {
    return CatchyTrajectoryRegionQuery.timeInvariant(RegionUnion.wrap( //
        new PolygonRegion(Tensors.matrixDouble(new double[][] { //
            { 0.633, -0.333 }, { 1.733, 0.517 }, { 1.617, 2.317 }, { 0.483, 3.317 }, //
            { -1.250, 3.167 }, { -1.383, 4.483 }, { 6.350, 4.400 }, { 6.250, -0.950 } //
        })), //
        new PolygonRegion(Tensors.matrixDouble(new double[][] { //
            { -0.717, 3.583 }, { -2.100, 1.517 }, { -3.167, 0.033 }, { -5.750, 0.017 }, { -5.517, 5.117 } //
        })), //
        new PolygonRegion(Tensors.matrixDouble(new double[][] { //
            { -6.933, 0.300 }, { -4.700, 0.250 }, { -4.617, -2.950 }, { 0.433, -3.217 }, //
            { 1.050, -0.300 }, { 1.867, -0.417 }, { 2.150, -5.300 }, { -6.900, -4.900 } //
        })) //
    ));
  }

  static TrajectoryPlanner createPlanner(CoordinateWrap coordinateWrap, So2Region so2Region) {
    Tensor eta = Tensors.vector(3, 3, 50 / Math.PI);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        Se2CarIntegrator.INSTANCE, Se2StateSpaceModel.INSTANCE, RationalScalar.of(1, 6), 5);
    FlowsInterface carFlows = Se2CarFlows.forward(RealScalar.ONE, Degree.of(45));
    Collection<Tensor> controls = carFlows.getFlows(6);
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(new Se2ComboRegion( //
        new BallRegion(Tensors.vector(-0.5, 0), RealScalar.of(0.5)), so2Region), //
        controls);
    TrajectoryRegionQuery obstacleQuery = obstacleQuery();
    // ---
    StateTimeRaster stateTimeRaster = new EtaRaster(eta, StateTimeTensorFunction.state(coordinateWrap::represent));
    return new StandardTrajectoryPlanner(stateTimeRaster, stateIntegrator, controls, //
        new TrajectoryObstacleConstraint(obstacleQuery), se2MinTimeGoalManager.getGoalInterface());
  }

  private static void demo(CoordinateWrap coordinateWrap, So2Region so2Region) {
    TrajectoryPlanner trajectoryPlanner = createPlanner(coordinateWrap, so2Region);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0.1, 0, 0), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(4000);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlGui.glc(trajectoryPlanner);
  }

  public static void main(String[] args) {
    demo(Se2CoveringWrap.INSTANCE, So2Region.covering(RealScalar.ZERO, RealScalar.of(0.3)));
    demo(Se2Wrap.INSTANCE, So2Region.periodic(RealScalar.ZERO, RealScalar.of(0.3)));
  }
}
