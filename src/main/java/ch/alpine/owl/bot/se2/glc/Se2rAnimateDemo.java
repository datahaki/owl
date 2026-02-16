// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.Collection;

import ch.alpine.owl.bot.se2.Se2CarIntegrator;
import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.bot.se2.Se2MinTimeGoalManager;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.region.HyperplaneRegion;
import ch.alpine.owl.region.RegionUnion;
import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.owl.util.win.OwlFrame;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Rational;
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
        Se2CarIntegrator.INSTANCE, Se2StateSpaceModel.INSTANCE, Rational.of(1, 6), 5);
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, Degree.of(45));
    Collection<Tensor> controls = carFlows.getFlows(6);
    // place holder for parameter class
    Se2ComboRegion se2ComboRegion = //
        Se2ComboRegion.ball(Tensors.vector(-1, -1, Math.PI * 2), Tensors.vector(0.1, 0.1, 0.17));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager( //
        se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    Region<Tensor> region = RegionUnion.wrap( //
        new HyperplaneRegion(Tensors.vector(0, -1, 0), RealScalar.of(1.5)), //
        new HyperplaneRegion(Tensors.vector(0, +1, 0), RealScalar.of(2.0)) //
    );
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(region));
    // ---
    return new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), stateIntegrator, controls, plannerConstraint, goalInterface);
  }

  static void main() throws Exception {
    TrajectoryPlanner trajectoryPlanner = trajectoryPlanner();
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(3), RealScalar.ZERO));
    OwlFrame owlFrame = OwlGui.start();
    owlFrame.geometricComponent.setOffset(169, 71);
    owlFrame.jFrame.setBounds(100, 100, 300, 200);
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    while (!trajectoryPlanner.getBest().isPresent() && owlFrame.jFrame.isVisible()) {
      glcExpand.findAny(1);
      owlFrame.setGlc(trajectoryPlanner);
      Thread.sleep(100);
    }
  }
}
