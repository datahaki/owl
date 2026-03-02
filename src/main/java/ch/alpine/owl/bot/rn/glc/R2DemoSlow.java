// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import ch.alpine.ascony.io.AnimationWriter;
import ch.alpine.ascony.io.GifAnimationWriter;
import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.bot.r2.R2Bubbles;
import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.util.ren.RegionRenderFactory;
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
import ch.alpine.owlets.math.state.EmptyTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TrajectoryRegionQuery;
import ch.alpine.sophis.flow.Integrators;
import ch.alpine.sophis.flow.StateSpaceModels;
import ch.alpine.sophis.reg.BallRegion;
import ch.alpine.sophis.reg.EllipsoidRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Ramp;

/* package */ enum R2DemoSlow {
  ;
  static TrajectoryPlanner simpleEmpty() throws Exception {
    return simple(EmptyTrajectoryRegionQuery.INSTANCE);
  }

  static TrajectoryPlanner simpleR2Bubbles() throws Exception {
    return simple(CatchyTrajectoryRegionQuery.timeInvariant(R2Bubbles.INSTANCE));
  }

  static TrajectoryPlanner simpleR2Circle() throws Exception {
    return simple(CatchyTrajectoryRegionQuery.timeInvariant( //
        new EllipsoidRegion(Tensors.vector(-1, 0), Tensors.vector(2, 2))));
  }

  private static TrajectoryPlanner simple(TrajectoryRegionQuery obstacleQuery) throws Exception {
    Tensor stateRoot = Tensors.vector(-2.2, -2.2);
    Tensor stateGoal = Tensors.vector(2, 3.5);
    Scalar radius = RealScalar.of(0.8);
    Tensor eta = Tensors.vector(1.5, 1.5);
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        Integrators.EULER, StateSpaceModels.SINGLE_INTEGRATOR, Quantity.of(Rational.of(1, 5), "s"), 5);
    R2Flows r2Flows = new R2Flows(Quantity.of(1, "s^-1"));
    Collection<Tensor> controls = r2Flows.getFlows(6);
    BallRegion ballRegion = new BallRegion(stateGoal, radius);
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    RenderInterface renderInterface = RegionRenderFactory.create(obstacleQuery);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(obstacleQuery);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, Quantity.of(0, "s")));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures.resolve("R2_Slow.gif"), 400, TimeUnit.MILLISECONDS)) {
      for (int i = 0; i < 20; ++i) {
        Optional<GlcNode> optional = trajectoryPlanner.getBest();
        if (optional.isPresent())
          break;
        glcExpand.findAny(1);
        TimerFrame owlFrame = OwlGui.glc(trajectoryPlanner);
        owlFrame.geometricComponent.addRenderInterfaceBackground(RegionRenderFactory.create(ballRegion));
        owlFrame.geometricComponent.addRenderInterfaceBackground(renderInterface); // reference to collection
        animationWriter.write(owlFrame.offscreen());
      }
    }
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      GlcNode goalNode = optional.orElseThrow();
      Scalar cost = goalNode.costFromRoot();
      Scalar lowerBound = Ramp.FUNCTION.apply(Vector2Norm.between(stateGoal, stateRoot).subtract(radius));
      if (Scalars.lessThan(cost, lowerBound))
        throw new Throw(cost, lowerBound);
    }
    return trajectoryPlanner;
  }

  static void demo(TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    // OwlyGui.glc(trajectoryPlanner);
  }

  static void main() throws Exception {
    simpleR2Circle();
  }
}
