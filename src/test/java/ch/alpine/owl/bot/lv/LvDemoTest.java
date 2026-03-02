// code by jph
package ch.alpine.owl.bot.lv;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.HeuristicAssert;
import ch.alpine.owlets.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.StateTimeTensorFunction;
import ch.alpine.sophis.flow.Integrators;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.reg.EllipsoidRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.exp.Log;

class LvDemoTest {
  @Test
  void testPlan() {
    for (int index = 0; index < 5; ++index) {
      Tensor eta = Tensors.vector(10, 10);
      StateSpaceModel stateSpaceModel = LvStateSpaceModel.of(1, 2);
      StateIntegrator stateIntegrator = new FixedStateIntegrator( //
          Integrators.RK45, stateSpaceModel, Quantity.of(Rational.of(1, 30), "s"), 4);
      Collection<Tensor> controls = LvControls.create(2);
      EllipsoidRegion ellipsoidRegion = new EllipsoidRegion(Tensors.vector(2, 1), Tensors.vector(0.1, 0.1));
      GoalInterface goalInterface = new LvGoalInterface(ellipsoidRegion);
      // ---
      StateTimeRaster stateTimeRaster = new EtaRaster(eta, StateTimeTensorFunction.state(tensor -> tensor.maps(Log.FUNCTION)));
      TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
          stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
      // ---
      // trajectoryPlanner.represent = StateTimeTensorFunction.state(Log::of);
      trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(2, 0.3), Quantity.of(0, "s")));
      GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
      glcExpand.findAny(10_000);
      if (glcExpand.getExpandCount() < 9800) {
        HeuristicAssert.check(trajectoryPlanner);
        // TrajectoryPlannerConsistency.check(trajectoryPlanner);
        return;
      }
    }
    fail();
  }
}
