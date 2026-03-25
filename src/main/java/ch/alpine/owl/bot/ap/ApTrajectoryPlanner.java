// code by astoll
package ch.alpine.owl.bot.ap;

import java.util.Collection;

import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTimeTensorFunction;
import ch.alpine.sophis.flow.TimeIntegrator;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.qty.Quantity;

class ApTrajectoryPlanner {
  /* Setting up parameters for the ApComboRegion
   * Note: GOAL and RADIUS_VECTOR are 3D, since x is omitted in ApComboRegion */
  final static Tensor GOAL = Tensors.fromString("{5[m], 60[m*s^-1], 0}"); // goal = {zCenter, vCenter, gammaCenter}
  final static Tensor RADIUS_VECTOR = Tensors.fromString("{5[m], 200[m*s^-1], 0.8}"); // radius_vector = {zRadius, vRadius, GammaRadius 50[deg]}
  /* Creation of control flows */
  final static Scalar MAX_AOA = ApStateSpaceModel.MAX_AOA;
  final static int THRUST_PARTIONING = 3;
  final static Tensor THRUSTS = Subdivide.of( //
      ApStateSpaceModel.MAX_THRUST.zero(), //
      ApStateSpaceModel.MAX_THRUST, //
      THRUST_PARTIONING);
  final static int FLOWRES = 2;
  final static FlowsInterface AP_FLOWS = ApFlows.of(MAX_AOA, THRUSTS);
  /* Setting up integrator */
  static final TimeIntegrator INTEGRATOR = TimeIntegrators.RK4;
  /* Setting up Time Raster */
  final static Tensor PARTITION_SCALE = Tensors.of(RealScalar.ONE, RealScalar.ONE, RealScalar.ONE, Degree.of(1)).unmodifiable();

  static protected StateTimeRaster stateTimeRaster() {
    return new EtaRaster(PARTITION_SCALE, StateTimeTensorFunction.state(ApWrap.INSTANCE::represent));
  }

  /** This function creates an object of the StandardTrajectoryPlanner class.
   * All necessary parameters are defined in {@link ApTrajectoryPlanner}
   * 
   * @return New StandardTrajectoryPlanner for airplane simulation */
  static StandardTrajectoryPlanner apStandardTrajectoryPlanner() {
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        INTEGRATOR, ApStateSpaceModel.INSTANCE, Quantity.of(Rational.of(1, 5), "s"), 3);
    Collection<Tensor> controls = AP_FLOWS.getFlows(FLOWRES);
    ApComboRegion apComboRegion = ApComboRegion.createApRegion(GOAL, RADIUS_VECTOR);
    ApMinTimeGoalManager apMinTimeGoalManager = new ApMinTimeGoalManager(apComboRegion, ApStateSpaceModel.Z_DOT_FLIGHT_MAX);
    GoalInterface goalInterface = apMinTimeGoalManager.getGoalInterface();
    PlannerConstraint apPlannerConstraint = ApPlannerConstraint.INSTANCE;
    return new StandardTrajectoryPlanner(stateTimeRaster(), stateIntegrator, controls, apPlannerConstraint, goalInterface);
  }
}
