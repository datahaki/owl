// code by jph
package ch.alpine.owl.bot.ap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.owlets.math.flow.Integrator;
import ch.alpine.owlets.math.flow.RungeKutta4Integrator;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.qty.Quantity;

class ApFlowsTest {
  @Test
  void testSimple() {
    FlowsInterface flowsInterface = ApFlows.of(Degree.of(10), Tensors.vector(0, 1, 2));
    Collection<Tensor> collection = flowsInterface.getFlows(10);
    assertEquals(collection.size(), 33);
  }

  @Test
  void testUnit() {
    Integrator INTEGRATOR = RungeKutta4Integrator.INSTANCE;
    Scalar MAX_AOA = ApStateSpaceModel.MAX_AOA;
    int THRUST_PARTIONING = 3;
    Tensor THRUSTS = Subdivide.of( //
        ApStateSpaceModel.MAX_THRUST.zero(), //
        ApStateSpaceModel.MAX_THRUST, //
        THRUST_PARTIONING);
    FlowsInterface AP_FLOWS = ApFlows.of(MAX_AOA, THRUSTS);
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        INTEGRATOR, ApStateSpaceModel.INSTANCE, Quantity.of(Rational.of(1, 5), "s"), 3);
    Collection<Tensor> controls = AP_FLOWS.getFlows(2);
    Tensor INITIAL = Tensors.fromString("{0[m], 80[m], 60[m*s^-1], -0.02}");
    StateTime stateTime = new StateTime(INITIAL, Quantity.of(0, "s"));
    Collection<Tensor> flows = AP_FLOWS.getFlows(3);
    for (Tensor u : flows)
      stateIntegrator.trajectory(stateTime, u);
  }
}
