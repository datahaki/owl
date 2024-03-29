// code by ynager
package ch.alpine.owl.glc.rl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.r2.R2RationalFlows;
import ch.alpine.owl.glc.adapter.ConstraintViolationCost;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.adapter.VectorCostGoalAdapter;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.math.VectorScalar;
import ch.alpine.owl.math.VectorScalars;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Sign;

class RLTrajectoryPlannerTest {
  private static GlcNode _withSlack(String name, Tensor slacks) {
    final Tensor stateRoot = Tensors.vector(0, 0);
    final Tensor stateGoal = Tensors.vector(5, 0);
    // ---
    int n = 3;
    Tensor eta = Tensors.vector(n, n);
    // radius is chosen so that goal region contains at least one domain entirely
    final Scalar radius = RealScalar.of(Math.sqrt(2) / n);
    StateIntegrator stateIntegrator = //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 5), 5);
    R2Flows r2Flows = new R2RationalFlows(RationalScalar.HALF);
    Collection<Tensor> controls = r2Flows.getFlows(13);
    RegionWithDistance<Tensor> goalRegion = new BallRegion(stateGoal, radius);
    // the 1st cost penalizes distance of path with slack
    CostFunction distanceCost = new CostFunction() {
      @Override // from CostIncrementFunction
      public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
        return Vector2Norm.between(glcNode.stateTime().state(), trajectory.getLast().state()); // ||x_prev - x_next||
      }

      @Override // from HeuristicFunction
      public Scalar minCostToGoal(Tensor x) {
        return goalRegion.distance(x);
      }
    };
    // the 2nd cost penalizes membership in region
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(1, ""));
    // ---
    // the 3rd cost penalizes distance of path
    GoalInterface goalInterface = //
        new VectorCostGoalAdapter(Arrays.asList(distanceCost, regionCost, distanceCost), goalRegion);
    // ---
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    RLTrajectoryPlanner trajectoryPlanner = new StandardRLTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface, slacks);
    assertEquals(trajectoryPlanner.getStateIntegrator(), stateIntegrator);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.untilOptimal(1000);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    assertTrue(optional.isPresent()); // guarantee optimal solution exists
    GlcNode goalNode = optional.get();
    VectorScalar cost = (VectorScalar) goalNode.costFromRoot();
    Scalar lowerBound = goalRegion.distance(stateRoot);
    Scalar marginDist = cost.vector().Get(0).subtract(lowerBound);
    Sign.requirePositiveOrZero(marginDist);
    // ---
    GlcNode minCostNode = StaticHelper.getMin(trajectoryPlanner.reachingSet.collection(), 0);
    Tensor minComp = VectorScalars.vector(minCostNode.merit()); // min cost component in goal
    Scalar upperBound = minComp.Get(0).add(slacks.Get(0));
    assertTrue(Scalars.lessEquals(cost.vector().Get(0), upperBound));
    // List<StateTime> pathFromRootTo =
    GlcNodes.getPathFromRootTo(goalNode);
    // pathFromRootTo.stream().map(StateTime::toInfoString).forEach(System.out::println);
    Map<Tensor, RLDomainQueue> rlDomainQueueMap = trajectoryPlanner.getRLDomainQueueMap();
    DQMInspection dqmIntrospection = new DQMInspection(rlDomainQueueMap);
    Tensor count = dqmIntrospection.getCount();
    // Tensor image =
    Raster.of(count, ColorDataGradients.CLASSIC);
    // File file = HomeDirectory.Pictures(name + ".png");
    // Export.of(file, ImageResize.nearest(image, 4));
    // System.out.println("domain queue map size=" + rlDomainQueueMap.size());
    // rlDomainQueueMap.keySet().forEach(System.out::println);
    return goalNode;
  }

  @Test
  void testSix() {
    GlcNode goalNode = _withSlack("slack6", Tensors.vector(6, 0, 0));
    // Tensor costFromRoot =
    VectorScalars.vector(goalNode.costFromRoot());
    // System.out.println(costFromRoot);
    // assertEquals(costFromRoot, Tensors.vector(9, 2, 9));
  }

  @Test
  void testFour() {
    GlcNode goalNode = _withSlack("slack4", Tensors.vector(4, 0, 0));
    // Tensor costFromRoot =
    VectorScalars.vector(goalNode.costFromRoot());
    // System.out.println(costFromRoot);
    // {8.493277857749513, 0, 8.493277857749513}
    // {8.994695536912735, 1, 8.994695536912735}
    // {8.496051174695095, 0, 8.496051174695095}
    // assertEquals(costFromRoot, Tensors.vector(9, 2, 9));
  }

  @Test
  void testTwo() {
    GlcNode goalNode = _withSlack("slack2", Tensors.vector(2, 0, 0));
    // Tensor costFromRoot =
    VectorScalars.vector(goalNode.costFromRoot());
    // System.out.println(costFromRoot);
    // assertEquals(costFromRoot, Tensors.vector(7, 3, 7));
  }

  @Test
  void testZeroEbbes() {
    GlcNode goalNode = _withSlack("slack1", Tensors.vector(1.3, 0, 0));
    // Tensor costFromRoot =
    VectorScalars.vector(goalNode.costFromRoot());
    // System.out.println(costFromRoot);
    // assertEquals(costFromRoot, Tensors.vector(5, 4, 5));
  }

  @Test
  void testZero() {
    GlcNode bestNode = _withSlack("slack0", Tensors.vector(0, 0, 0));
    // Tensor costFromRoot =
    VectorScalars.vector(bestNode.costFromRoot());
    // System.out.println(costFromRoot);
    // assertEquals(costFromRoot, Tensors.vector(5, 4, 5));
  }
}
