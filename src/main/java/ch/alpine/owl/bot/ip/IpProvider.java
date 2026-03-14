// code by jph
package ch.alpine.owl.bot.ip;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.bridge.pro.VoidProvider;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.sophis.reg.FreeBoundedIntervalRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class IpProvider implements VoidProvider {
  TrajectoryPlanner trajectoryPlanner = null;

  @Override
  public Void runStandalone() {
    StateSpaceModel stateSpaceModel = IpStateSpaceModel.EXAMPLE; // g;
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        TimeIntegrators.RK45, stateSpaceModel, Quantity.of(Rational.of(1, 12), "s"), 5);
    Collection<Tensor> controls = IpControls.createControls(Quantity.of(10, "N"), 20);
    IpGoalManager ipGoalManager = new IpGoalManager( //
        Tensors.fromString("{2[m],0[m*s^-1],0,0[s^-1]}"), //
        Tensors.fromString("{0.1[m],0.1[m*s^-1],0.3,0.3[s^-1]}"));
    Clip clip_pos = Clips.interval(Quantity.of(-1, "m"), Quantity.of(3, "m"));
    MemberQ region = MemberQ.any(List.of( //
        new FreeBoundedIntervalRegion(0, clip_pos), // ,
        new FreeBoundedIntervalRegion(2, Clips.interval(-2, +2)) // angle
    ));
    PlannerConstraint plannerConstraint = RegionConstraints.timeDependent(region);
    Tensor eta = Tensors.vector(10, 10, 100, 100);
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, plannerConstraint, ipGoalManager);
    Tensor initial = Tensors.fromString("{0[m],0[m*s^-1],0,0[s^-1]}");
    trajectoryPlanner.insertRoot(new StateTime(initial, Quantity.of(0, "s")));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    // glcExpand.steps(50);
    glcExpand.findAny(3000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    return null;
  }

  public boolean isContinued() {
    return true;
  }

  static void main() {
    new IpProvider().runStandalone();
  }
}
