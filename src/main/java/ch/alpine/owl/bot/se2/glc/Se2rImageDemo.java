// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.bot.se2.Se2CarIntegrator;
import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.bot.se2.Se2MinTimeGoalManager;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.OwlFrame;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.qty.Degree;

/** (x, y, theta) */
enum Se2rImageDemo {
  ;
  public static void main(String[] args) throws Exception {
    Region<Tensor> region = //
        ImageRegions.loadFromRepository("/io/track0_100.png", Tensors.vector(8, 8), false);
    Tensor partitionScale = Tensors.vector(3, 3, 50 / Math.PI);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        Se2CarIntegrator.INSTANCE, Se2StateSpaceModel.INSTANCE, RationalScalar.of(1, 6), 5);
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
    OwlFrame owlFrame = OwlGui.start();
    owlFrame.geometricComponent.setOffset(100, 550);
    owlFrame.jFrame.setBounds(100, 100, 700, 700);
    owlFrame.addBackground(RegionRenders.create(region));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    while (!trajectoryPlanner.getBest().isPresent() && owlFrame.jFrame.isVisible()) {
      glcExpand.findAny(1000);
      owlFrame.setGlc(trajectoryPlanner);
      Thread.sleep(10);
    }
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
  }
}
