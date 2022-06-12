// code by astoll, jph
package ch.alpine.owl.bot.rn.glc;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

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
import ch.alpine.owl.glc.rl2.RelaxedGlcExpand;
import ch.alpine.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.alpine.owl.glc.rl2.StandardRelaxedLexicographicPlanner;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.ren.BallRegionRender;
import ch.alpine.owl.util.ren.PolygonRegionRender;
import ch.alpine.owl.util.win.OwlFrame;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;

/** demo shows that more slack results in a larger search effort */
public class RelaxedTrajectoryExpandDemo {
  // -------- slacks --------
  final Tensor slacks = Tensors.vector(1, 0);
  // -------- stateTimeRaster --------
  int n = 4;
  final Tensor eta = Tensors.vector(n, n);
  // -------- StateIntegrator --------
  final Scalar timeStep = RationalScalar.of(3, 7);
  final StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
      EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, timeStep, 1);
  // -------- GoalInterface --------
  final Tensor stateGoal = Tensors.vector(5, 0);
  final Scalar radius = RealScalar.of(Math.sqrt(2) / n);
  final RegionWithDistance<Tensor> regionWithDistance = new BallRegion(stateGoal, radius);
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
    CostFunction costFunction = new RnMinDistGoalManager(regionWithDistance);
    // --
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(polygonRegion);
    CostFunction regionCost = ConstraintViolationCost.of(plannerConstraint, Quantity.of(2, ""));
    // ---
    GoalInterface goalInterface = //
        new VectorCostGoalAdapter(List.of(costFunction, regionCost), regionWithDistance);
    // -------------------------------
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface, slacks);
  }

  public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException {
    RelaxedTrajectoryExpandDemo relaxedTrajectoryPlanner1Demo = new RelaxedTrajectoryExpandDemo();
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = relaxedTrajectoryPlanner1Demo.createPlanner();
    Serialization.copy(relaxedTrajectoryPlanner);
    final Tensor stateRoot = Tensors.vector(0.1, 0.1);
    relaxedTrajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    RelaxedGlcExpand glcExpand = new RelaxedGlcExpand(relaxedTrajectoryPlanner);
    OwlFrame owlFrame = OwlGui.start();
    owlFrame.addBackground(new PolygonRegionRender(relaxedTrajectoryPlanner1Demo.polygonRegion));
    owlFrame.addBackground(new BallRegionRender((BallRegion) relaxedTrajectoryPlanner1Demo.regionWithDistance));
    while (!relaxedTrajectoryPlanner.getBest().isPresent() && owlFrame.jFrame.isVisible()) {
      glcExpand.findAny(1);
      owlFrame.setGlc(relaxedTrajectoryPlanner);
      // animationWriter.append(owlyFrame.offscreen());
      Thread.sleep(1);
    }
  }
}