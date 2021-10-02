// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.Arrays;
import java.util.Collection;

import ch.alpine.owl.bot.se2.Se2CarIntegrator;
import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.bot.se2.Se2MinTimeGoalManager;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.gui.win.OwlyFrame;
import ch.alpine.owl.gui.win.OwlyGui;
import ch.alpine.owl.math.region.HyperplaneRegion;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.qty.Degree;

enum Se2rAnimateDemo {
  ;
  public static TrajectoryPlanner trajectoryPlanner() {
    Tensor eta = Tensors.vector(6, 6, 50 / Math.PI);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        Se2CarIntegrator.INSTANCE, Se2StateSpaceModel.INSTANCE, RationalScalar.of(1, 6), 5);
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, Degree.of(45));
    Collection<Tensor> controls = carFlows.getFlows(6);
    // place holder for parameter class
    Se2ComboRegion se2ComboRegion = //
        Se2ComboRegion.ball(Tensors.vector(-1, -1, Math.PI * 2), Tensors.vector(0.1, 0.1, 0.17));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager( //
        se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    Region<Tensor> region = RegionUnion.wrap(Arrays.asList( //
        new HyperplaneRegion(Tensors.vector(0, -1, 0), RealScalar.of(1.5)), //
        new HyperplaneRegion(Tensors.vector(0, +1, 0), RealScalar.of(2.0)) //
    ));
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(region));
    // ---
    return new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), stateIntegrator, controls, plannerConstraint, goalInterface);
  }

  public static void main(String[] args) throws Exception {
    TrajectoryPlanner trajectoryPlanner = trajectoryPlanner();
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(3), RealScalar.ZERO));
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.geometricComponent.setOffset(169, 71);
    owlyFrame.jFrame.setBounds(100, 100, 300, 200);
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    while (!trajectoryPlanner.getBest().isPresent() && owlyFrame.jFrame.isVisible()) {
      glcExpand.findAny(1);
      owlyFrame.setGlc(trajectoryPlanner);
      Thread.sleep(100);
    }
  }
}
