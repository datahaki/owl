// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.owl.bot.r2.R2Bubbles;
import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
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
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.state.EmptyTrajectoryRegionQuery;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.OwlFrame;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.AnimationWriter;
import ch.alpine.tensor.io.GifAnimationWriter;
import ch.alpine.tensor.nrm.Vector2Norm;
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
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 5), 5);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(6);
    BallRegion ballRegion = new BallRegion(stateGoal, radius);
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    RenderInterface renderInterface = RegionRenders.create(obstacleQuery);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(obstacleQuery);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures("R2_Slow.gif"), 400, TimeUnit.MILLISECONDS)) {
      OwlFrame owlFrame = OwlGui.start();
      owlFrame.addBackground(RegionRenders.create(ballRegion));
      owlFrame.addBackground(renderInterface); // reference to collection
      for (int i = 0; i < 20; ++i) {
        Optional<GlcNode> optional = trajectoryPlanner.getBest();
        if (optional.isPresent())
          break;
        glcExpand.findAny(1);
        owlFrame.setGlc(trajectoryPlanner);
        animationWriter.write(owlFrame.offscreen());
      }
      for (int i = 0; i < 4; ++i)
        animationWriter.write(owlFrame.offscreen());
    }
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      GlcNode goalNode = optional.orElseThrow();
      Scalar cost = goalNode.costFromRoot();
      Scalar lowerBound = Ramp.of(Vector2Norm.between(stateGoal, stateRoot).subtract(radius));
      if (Scalars.lessThan(cost, lowerBound))
        throw TensorRuntimeException.of(cost, lowerBound);
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

  public static void main(String[] args) throws Exception {
    simpleR2Circle();
  }
}
