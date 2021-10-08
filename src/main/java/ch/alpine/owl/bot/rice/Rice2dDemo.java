// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.java.win.OwlFrame;
import ch.alpine.java.win.OwlGui;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.GlcTrajectories;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.gui.ren.TrajectoryRender;
import ch.alpine.owl.math.flow.MidpointIntegrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.region.HyperplaneRegion;
import ch.alpine.owl.math.region.RegionUnion;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;

/** position and velocity control in 2D with friction */
/* package */ enum Rice2dDemo {
  ;
  static final EllipsoidRegion ELLIPSOID_REGION = //
      new EllipsoidRegion(Tensors.vector(3, 3, -1, 0), Tensors.vector(0.5, 0.5, 0.4, 0.4));

  public static TrajectoryPlanner createInstance(Scalar mu, StateSpaceModel stateSpaceModel) {
    Tensor eta = Tensors.vector(3, 3, 6, 6);
    Collection<Tensor> controls = Rice2Controls.create2d(1).getFlows(15);
    GoalInterface goalInterface = new Rice2GoalManager(ELLIPSOID_REGION);
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(RegionUnion.wrap(Arrays.asList( //
            new HyperplaneRegion(Tensors.vector(1, +0, 0, 0), RealScalar.ZERO), //
            new HyperplaneRegion(Tensors.vector(0, +1, 0, 0), RealScalar.ZERO), //
            new HyperplaneRegion(Tensors.vector(0, -1, 0, 0), RealScalar.of(3.2)), //
            new HyperplaneRegion(Tensors.vector(0, +0, 0, 1), RealScalar.ZERO) //
        ))));
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, FixedStateIntegrator.create( //
            MidpointIntegrator.INSTANCE, stateSpaceModel, RationalScalar.HALF, 5),
        controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0.1, 0.1, 0, 0), RealScalar.ZERO));
    return trajectoryPlanner;
  }

  // Hint: ensure that goal region contains at least 1 domain etc.
  public static void main(String[] args) {
    Scalar mu = RealScalar.of(-.5);
    StateSpaceModel stateSpaceModel = Rice2StateSpaceModel.of(mu);
    TrajectoryPlanner trajectoryPlanner = createInstance(mu, stateSpaceModel);
    Timing timing = Timing.started();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    // 550 1.6898229210000002 without parallel integration of trajectories
    // 555 1.149214356 with parallel integration of trajectories
    System.out.println(glcExpand.getExpandCount() + " " + timing.seconds());
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    OwlFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    if (optional.isPresent()) {
      GlcNode glcNode = optional.orElseThrow();
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(glcNode);
      StateTimeTrajectories.print(trajectory);
      List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(FixedStateIntegrator.create( //
          MidpointIntegrator.INSTANCE, stateSpaceModel, RationalScalar.HALF, 5), glcNode);
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(samples);
      owlFrame.addBackground(trajectoryRender);
    }
    glcExpand.untilOptimal(1000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      GlcNode glcNode = optional.orElseThrow();
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(glcNode);
      StateTimeTrajectories.print(trajectory);
      List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(FixedStateIntegrator.create( //
          MidpointIntegrator.INSTANCE, stateSpaceModel, RationalScalar.HALF, 5), glcNode);
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(samples);
      owlFrame.addBackground(trajectoryRender);
    }
    owlFrame.addBackground(RegionRenders.create(ELLIPSOID_REGION));
  }
}
