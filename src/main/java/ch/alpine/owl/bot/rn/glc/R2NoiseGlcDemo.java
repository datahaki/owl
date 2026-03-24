// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.r2.R2NoiseCostFunction;
import ch.alpine.owl.bot.r2.R2NoiseRegion;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.MultiCostGoalAdapter;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TrajectoryRegionQuery;
import ch.alpine.sophis.flow.StateSpaceModels;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.sophis.reg.BallRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Timing;

/** expands: 1491
 * computation time: 0.876993604 */
/* package */ enum R2NoiseGlcDemo {
  ;
  static void main() {
    Tensor partitionScale = Tensors.vector(8, 8);
    final Scalar threshold = RealScalar.of(0.1);
    MemberQ region = new R2NoiseRegion(threshold);
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        TimeIntegrators.EULER, StateSpaceModels.SINGLE_INTEGRATOR, Quantity.of(Rational.of(1, 12), "s"), 4);
    R2Flows r2Flows = new R2Flows(Quantity.of(1, "s^-1"));
    Collection<Tensor> controls = r2Flows.getFlows(23);
    final Tensor center = Tensors.vector(10, 0);
    final Scalar radius = RealScalar.of(0.2);
    BallRegion ballRegion = new BallRegion(center, radius);
    GoalInterface goalInterface = MultiCostGoalAdapter.of( //
        new RnMinDistGoalManager(ballRegion), //
        List.of(new R2NoiseCostFunction(threshold.subtract(RealScalar.of(0.3)))));
    TrajectoryRegionQuery trajectoryRegionQuery = CatchyTrajectoryRegionQuery.timeInvariant(region);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(partitionScale), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), Quantity.of(0, "s")));
    Timing timing = Timing.started();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(10_000);
    System.out.println(glcExpand.getExpandCount() + " " + timing.seconds());
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    TimerFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    owlFrame.geometricComponent().addRenderInterfaceBackground(RegionRenderFactory.create(ballRegion));
    owlFrame.geometricComponent().addRenderInterfaceBackground(RegionRenderFactory.create(trajectoryRegionQuery));
  }
}
