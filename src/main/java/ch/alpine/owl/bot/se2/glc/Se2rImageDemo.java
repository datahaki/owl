// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.bot.se2.Se2CarIntegrator;
import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.bot.se2.Se2MinTimeGoalManager;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.owl.util.ren.RegionRenders;
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
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.qty.Quantity;

/** (x, y, theta) */
enum Se2rImageDemo {
  ;
  static void main() throws Exception {
    MemberQ region = //
        ImageRegions.loadFromRepository("/io/track0_100.png", Tensors.vector(8, 8), false);
    Tensor partitionScale = Tensors.vector(3, 3, 50 / Math.PI);
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        Se2CarIntegrator.INSTANCE, Se2StateSpaceModel.INSTANCE, Quantity.of(Rational.of(1, 6), "s"), 5);
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, Degree.of(45));
    Collection<Tensor> controls = carFlows.getFlows(6);
    Se2ComboRegion se2ComboRegion = //
        Se2ComboRegion.ball(Tensors.vector(4.0, 5.6, 0), Tensors.vector(0.1, 0.1, 0.17));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager( //
        se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(region));
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(partitionScale), stateIntegrator, controls, plannerConstraint, goalInterface);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(3), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    while (!trajectoryPlanner.getBest().isPresent()) {
      glcExpand.findAny(1000);
    }
    TimerFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    owlFrame.geometricComponent.setOffset(100, 550);
    owlFrame.jFrame.setBounds(100, 100, 700, 700);
    owlFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(region));
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
  }
}
