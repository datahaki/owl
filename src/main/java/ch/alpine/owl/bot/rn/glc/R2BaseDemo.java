// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.region.BallRegion;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.flow.EulerIntegrator;
import ch.alpine.owlets.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;

abstract class R2BaseDemo implements DemoInterface {
  protected abstract MemberQ region();

  protected abstract Tensor startState();

  @Override // from DemoInterface
  public final TimerFrame getTimerFrame() {
    Tensor partitionScale = Tensors.vector(5, 5);
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, Quantity.of(Rational.of(1, 8), "s"), 4);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(20);
    BallRegion ballRegion = new BallRegion(Tensors.vector(5, 5), RealScalar.of(0.2));
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    MemberQ region = region(); //
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(region));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(partitionScale), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(startState(), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1500);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    TimerFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    owlFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(region));
    owlFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(ballRegion));
    owlFrame.geometricComponent.setOffset(250, 500);
    return owlFrame;
  }
}
