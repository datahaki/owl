// code by astoll
package ch.alpine.owl.glc.rl2;

import java.util.Arrays;
import java.util.Collection;

import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.r2.R2RationalFlows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.glc.adapter.ConstraintViolationCost;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.adapter.VectorCostGoalAdapter;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

/* package */ enum TestHelper {
  ;
  /* package */ static RelaxedTrajectoryPlanner createPlanner() {
    // -------- slacks --------
    final Tensor slacks = Tensors.vector(5, 2);
    // -------- stateTimeRaster --------
    int n = 2;
    final Tensor eta = Tensors.vector(n, n);
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    // -------- StateIntegrator --------
    Scalar timeStep = RationalScalar.of(4, 7);
    final StateIntegrator stateIntegrator = //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, timeStep, 1);
    // -------- controls --------
    R2Flows r2Flows = new R2RationalFlows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(4);
    for (Tensor flow : controls)
      ExactTensorQ.require(flow);
    // -------- GoalInterface --------
    final Tensor stateGoal = Tensors.vector(5, 0);
    final Scalar radius = RealScalar.of(Math.sqrt(2) / n);
    RegionWithDistance<Tensor> regionWithDistance = new BallRegion(stateGoal, radius);
    // ---
    CostFunction costFunction = new RnMinDistGoalManager(regionWithDistance);
    // ---
    Tensor polygon = Tensors.matrixFloat(new float[][] { { 1, 0 }, { 1, -10 }, { 4, -10 }, { 4, 3 } });
    PolygonRegion polygonRegion = new PolygonRegion(polygon);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(2, ""));
    // ---
    GoalInterface goalInterface = //
        new VectorCostGoalAdapter(Arrays.asList(costFunction, regionCost), regionWithDistance);
    // ---
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface, slacks);
  }
}
