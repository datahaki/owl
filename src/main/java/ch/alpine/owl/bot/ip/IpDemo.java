// code by jph
package ch.alpine.owl.bot.ip;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.flow.MidpointIntegrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.region.FreeBoundedIntervalRegion;
import ch.alpine.owl.region.RegionUnion;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.sophis.math.Region;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.Clips;

/** inverted pendulum */
/* package */ enum IpDemo {
  ;
  static void main() {
    Tensor eta = Tensors.vector(10, 10, 10, 10);
    StateSpaceModel stateSpaceModel = new IpStateSpaceModel( //
        RealScalar.of(0.3), // M
        RealScalar.of(0.2), // m
        RealScalar.of(0.5), // l
        RealScalar.of(1)); // g;
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, stateSpaceModel, Rational.of(1, 12), 5);
    Collection<Tensor> controls = IpControls.createControls(2, 10);
    IpGoalManager ipGoalManager = new IpGoalManager( //
        Tensors.vector(2, 0, 0, 0), //
        Tensors.vector(0.1, 0.1, 1, 1));
    Region<Tensor> region = RegionUnion.wrap( //
        new FreeBoundedIntervalRegion(0, Clips.interval(-1, +3)), // ,
        new FreeBoundedIntervalRegion(2, Clips.interval(-2, +2)) // ,
    );
    PlannerConstraint plannerConstraint = RegionConstraints.timeDependent(region);
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, plannerConstraint, ipGoalManager);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(4), RealScalar.ZERO));
    // new ExpandGlcFrame(trajectoryPlanner);
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(3000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    OwlGui.glc(trajectoryPlanner);
  }
}
