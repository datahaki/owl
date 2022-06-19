// code by jph
package ch.alpine.owl.bot.tn;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.CoordinateWrap;
import ch.alpine.owl.math.SimpleTensorMetric;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeTensorFunction;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.sophus.math.api.TensorMetric;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;

/** topological torus */
enum T2Demo {
  ;
  public static void main(String[] args) {
    Tensor eta = Tensors.vector(4, 5);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RationalScalar.of(1, 10), 5);
    R2Flows r2Flows = new R2Flows(RealScalar.ONE);
    Collection<Tensor> controls = r2Flows.getFlows(36);
    CoordinateWrap coordinateWrap;
    coordinateWrap = new TnWrap(Tensors.vector(5, 7));
    // coordinateWrap = new IdentityWrap();
    TensorMetric tensorMetric = new SimpleTensorMetric(coordinateWrap);
    TnGoalManager rnGoal = new TnGoalManager(tensorMetric, Tensors.vector(4, 6), RealScalar.of(0.25));
    // performance depends on heuristic: zeroHeuristic vs rnGoal
    // Heuristic heuristic = new ZeroHeuristic(); // rnGoal
    // ---
    StateTimeRaster stateTimeRaster = new EtaRaster(eta, StateTimeTensorFunction.state(coordinateWrap::represent));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, rnGoal.getGoalInterface());
    // trajectoryPlanner.represent = StateTimeTensorFunction.state(coordinateWrap::represent);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1400);
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    OwlGui.glc(trajectoryPlanner);
  }
}
