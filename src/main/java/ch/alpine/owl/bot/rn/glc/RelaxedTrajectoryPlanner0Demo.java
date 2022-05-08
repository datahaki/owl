// code by astoll, ynager
package ch.alpine.owl.bot.rn.glc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import ch.alpine.bridge.win.BaseFrame;
import ch.alpine.java.win.DemoInterface;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.r2.R2RationalFlows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.data.tree.NodesAssert;
import ch.alpine.owl.glc.adapter.ConstraintViolationCost;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcTrajectories;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.adapter.VectorCostGoalAdapter;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.rl2.RelaxedDebugUtils;
import ch.alpine.owl.glc.rl2.RelaxedGlcExpand;
import ch.alpine.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.alpine.owl.glc.rl2.StandardRelaxedLexicographicPlanner;
import ch.alpine.owl.gui.ren.BallRegionRender;
import ch.alpine.owl.gui.ren.DomainQueueMapRender;
import ch.alpine.owl.gui.ren.EdgeRenders;
import ch.alpine.owl.gui.ren.EtaRender;
import ch.alpine.owl.gui.ren.PolygonRegionRender;
import ch.alpine.owl.gui.ren.TrajectoryRender;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.qty.Quantity;

public class RelaxedTrajectoryPlanner0Demo implements DemoInterface {
  // -------- slacks --------
  final Tensor slacks = Tensors.vector(2, 0);
  // -------- stateTimeRaster --------
  int n = 2;
  final Tensor eta = Tensors.vector(n, n);
  // -------- StateIntegrator --------
  final Scalar timeStep = RationalScalar.of(4, 7);
  final StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
      EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, timeStep, 1);
  // -------- GoalInterface --------
  final Tensor stateGoal = Tensors.vector(5, 0);
  final Scalar radius = RealScalar.of(Math.sqrt(2) / n);
  final RegionWithDistance<Tensor> goalRegion = new BallRegion(stateGoal, radius);
  private final Tensor polygon = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
  private final PolygonRegion polygonRegion = new PolygonRegion(polygon);

  public StandardRelaxedLexicographicPlanner createPlanner() {
    // -------- stateTimeRaster --------
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    // -------- controls --------
    R2Flows r2Flows = new R2RationalFlows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(7);
    for (Tensor flow : controls)
      ExactTensorQ.require(flow);
    // -------- GoalInterface --------
    // --
    CostFunction distanceCost = new RnMinDistGoalManager(goalRegion);
    // --
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(2, ""));
    // ---
    GoalInterface goalInterface = //
        new VectorCostGoalAdapter(Arrays.asList(distanceCost, regionCost), goalRegion);
    // -------------------------------
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface, slacks);
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = createPlanner();
    final Tensor stateRoot = Tensors.vector(0.1, 0.1);
    relaxedTrajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    RelaxedGlcExpand glcExpand = new RelaxedGlcExpand(relaxedTrajectoryPlanner);
    Timing timing = Timing.started();
    // glcExpand.findAny(1000);
    glcExpand.untilOptimal(1000);
    System.out.println("Execution Time: " + timing.seconds());
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    owlAnimationFrame.addBackground(new PolygonRegionRender(polygonRegion));
    owlAnimationFrame.addBackground(new EtaRender(eta));
    owlAnimationFrame.addBackground(DomainQueueMapRender.of(relaxedTrajectoryPlanner.getRelaxedDomainQueueMap().getMap(), eta));
    owlAnimationFrame.addBackground(new BallRegionRender((BallRegion) goalRegion));
    owlAnimationFrame.addBackground(EdgeRenders.of(relaxedTrajectoryPlanner));
    Optional<GlcNode> optional = relaxedTrajectoryPlanner.getBest();
    if (optional.isPresent()) {
      System.out.println(optional.orElseThrow().merit());
      Iterator<GlcNode> bestGoalNodes = relaxedTrajectoryPlanner.getAllNodesInGoal().iterator();
      while (bestGoalNodes.hasNext()) {
        GlcNode goalNode = bestGoalNodes.next();
        System.out.println(goalNode.merit());
        // System.out.println(goalNode.costFromRoot());
        List<TrajectorySample> trajectory = GlcTrajectories.detailedTrajectoryTo(stateIntegrator, goalNode);
        TrajectoryRender trajectoryRender = new TrajectoryRender();
        trajectoryRender.trajectory(trajectory);
        owlAnimationFrame.addBackground(trajectoryRender);
      }
    }
    // ---
    RelaxedDebugUtils.globalQueueSubsetOfQueuesInDomainMap(relaxedTrajectoryPlanner);
    RelaxedDebugUtils.nodeAmountCompare(relaxedTrajectoryPlanner);
    NodesAssert.allLeaf(relaxedTrajectoryPlanner.getQueue());
    // RelaxedDebugUtils.closeMatchesCheck(rlPlanner);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new RelaxedTrajectoryPlanner0Demo().start().jFrame.setVisible(true);
  }
}