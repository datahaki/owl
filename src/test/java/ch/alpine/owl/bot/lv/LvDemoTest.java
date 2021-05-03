// code by jph
package ch.alpine.owl.bot.lv;

import java.util.Collection;

import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.HeuristicAssert;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.StateTimeTensorFunction;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Log;
import junit.framework.TestCase;

public class LvDemoTest extends TestCase {
  public void testPlan() {
    for (int index = 0; index < 5; ++index) {
      Tensor eta = Tensors.vector(10, 10);
      StateSpaceModel stateSpaceModel = LvStateSpaceModel.of(1, 2);
      StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
          RungeKutta45Integrator.INSTANCE, stateSpaceModel, RationalScalar.of(1, 30), 4);
      Collection<Tensor> controls = LvControls.create(2);
      EllipsoidRegion ellipsoidRegion = new EllipsoidRegion(Tensors.vector(2, 1), Tensors.vector(0.1, 0.1));
      GoalInterface goalInterface = new LvGoalInterface(ellipsoidRegion);
      // ---
      StateTimeRaster stateTimeRaster = new EtaRaster(eta, StateTimeTensorFunction.state(Log::of));
      TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
          stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
      // ---
      // trajectoryPlanner.represent = StateTimeTensorFunction.state(Log::of);
      trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(2, 0.3), RealScalar.ZERO));
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
