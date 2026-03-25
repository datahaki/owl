// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.util.Collection;
import java.util.List;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TrajectoryRegionQuery;
import ch.alpine.sophis.flow.StateSpaceModels;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.sophis.reg.BallRegion;
import ch.alpine.sophis.reg.EllipsoidRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;

class R2SphereBase {
  private final MemberQ region1 = new EllipsoidRegion(Tensors.vector(3, 3), Tensors.vector(2, 2));
  private final MemberQ region2 = new EllipsoidRegion(Tensors.vector(2.5, 0), Tensors.vector(2, 1.5));
  private final BallRegion ballRegion = new BallRegion(Tensors.vector(5, 0), RealScalar.of(0.5));
  private final TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant( //
      MemberQ.any(List.of(region1, region2)));

  TrajectoryPlanner create() {
    Tensor partitionScale = Tensors.vector(3.5, 4);
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        TimeIntegrators.EULER, StateSpaceModels.SINGLE_INTEGRATOR, Quantity.of(Rational.of(1, 8), "s"), 5);
    R2Flows r2Flows = new R2Flows(Quantity.of(1, "s^-1"));
    Collection<Tensor> controls = r2Flows.getFlows(20);
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(partitionScale), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), Quantity.of(0, "s")));
    return trajectoryPlanner;
  }

  void show(TrajectoryPlanner trajectoryPlanner) {
    TimerFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    owlFrame.geometricComponent().addRenderInterfaceBackground(RegionRenderFactory.create(region1));
    owlFrame.geometricComponent().addRenderInterfaceBackground(RegionRenderFactory.create(region2));
    owlFrame.geometricComponent().addRenderInterfaceBackground(RegionRenderFactory.create(ballRegion));
    owlFrame.geometricComponent().addRenderInterfaceBackground(RegionRenderFactory.create(trajectoryRegionQuery));
  }
}
