// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.ren.TrajectoryRender;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.GlcTrajectories;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TrajectorySample;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.sophis.reg.EllipsoidRegion;
import ch.alpine.sophis.reg.HyperplaneRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Timing;

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
        new TrajectoryObstacleConstraint(SimpleTrajectoryRegionQuery.timeInvariant(MemberQ.any(List.of( //
            new HyperplaneRegion(Tensors.vector(1, +0, 0, 0), RealScalar.ZERO), //
            new HyperplaneRegion(Tensors.vector(0, +1, 0, 0), RealScalar.ZERO), //
            new HyperplaneRegion(Tensors.vector(0, -1, 0, 0), RealScalar.of(3.2)), //
            new HyperplaneRegion(Tensors.vector(0, +0, 0, 1), RealScalar.ZERO) //
        ))));
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, new FixedStateIntegrator( //
            TimeIntegrators.MIDPOINT, stateSpaceModel, Quantity.of(Rational.of(1, 2), "s"), 5),
        controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(0.1, 0.1, 0, 0), Quantity.of(0, "s")));
    return trajectoryPlanner;
  }

  // Hint: ensure that goal region contains at least 1 domain etc.
  static void main() {
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
    TimerFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    if (optional.isPresent()) {
      GlcNode glcNode = optional.orElseThrow();
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(glcNode);
      StateTimeTrajectories.print(trajectory);
      List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(new FixedStateIntegrator( //
          TimeIntegrators.MIDPOINT, stateSpaceModel, Quantity.of(Rational.of(1, 2), "s"), 5), glcNode);
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(samples);
      owlFrame.geometricComponent().addRenderInterfaceBackground(trajectoryRender);
    }
    glcExpand.untilOptimal(1000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      GlcNode glcNode = optional.orElseThrow();
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(glcNode);
      StateTimeTrajectories.print(trajectory);
      List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(new FixedStateIntegrator( //
          TimeIntegrators.MIDPOINT, stateSpaceModel, Quantity.of(Rational.of(1, 2), "s"), 5), glcNode);
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(samples);
      owlFrame.geometricComponent().addRenderInterfaceBackground(trajectoryRender);
    }
    owlFrame.geometricComponent().addRenderInterfaceBackground(RegionRenderFactory.create(ELLIPSOID_REGION));
  }
}
