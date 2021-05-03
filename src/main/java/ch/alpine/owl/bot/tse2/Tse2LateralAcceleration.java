// code by jl and jph
package ch.alpine.owl.bot.tse2;

import java.util.List;

import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.AbsSquared;

/** Paden thesis (5.5.13) on p.57
 * cost of lateral acceleration (believed to be correlated with rider comfort)
 * 2*u^2 where d_theta(t) = u(t)
 * 
 * in the implementation below we simply use the formula u^2 */
public enum Tse2LateralAcceleration implements CostFunction {
  INSTANCE;

  /** Curvature is changed angle over distance covered */
  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return cost(flow, StateTimeTrajectories.timeIncrement(glcNode, trajectory));
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO; // compatible with quantity addition of any unit
  }

  /** @param u for instance {2.5[m*s^-1], 0.0, 1.0[s^-1]}
   * @param dt for instance 0.5[s]
   * @return quantity with unit [s^-1] */
  public static Scalar cost(Tensor u, Scalar dt) {
    return AbsSquared.FUNCTION.apply(u.Get(0)).multiply(dt);
  }
}