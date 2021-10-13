// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.util.Arrays;
import java.util.Collection;

import ch.alpine.java.win.OwlFrame;
import ch.alpine.java.win.OwlGui;
import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;

/* package */ class R2SphereBase {
  private final Region<Tensor> region1 = new EllipsoidRegion(Tensors.vector(3, 3), Tensors.vector(2, 2));
  private final Region<Tensor> region2 = new EllipsoidRegion(Tensors.vector(2.5, 0), Tensors.vector(2, 1.5));
  private final BallRegion ballRegion = new BallRegion(Tensors.vector(5, 0), RealScalar.of(0.5));
  private final TrajectoryRegionQuery trajectoryRegionQuery = CatchyTrajectoryRegionQuery.timeInvariant( //
      RegionUnion.wrap(Arrays.asList(region1, region2)));

  TrajectoryPlanner create() {
    Tensor partitionScale = Tensors.vector(3.5, 4);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 8), 5);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(20);
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(partitionScale), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    return trajectoryPlanner;
  }

  void show(TrajectoryPlanner trajectoryPlanner) {
    OwlFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    owlFrame.addBackground(RegionRenders.create(region1));
    owlFrame.addBackground(RegionRenders.create(region2));
    owlFrame.addBackground(RegionRenders.create(ballRegion));
    owlFrame.addBackground(RegionRenders.create(trajectoryRegionQuery));
  }
}
