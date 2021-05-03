// code by ynager
package ch.alpine.owl.glc.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public class ConstraintViolationCost implements CostFunction, Serializable {
  /** Transforms the given planner constraint to a cost function by counting
   * constraint violations. A violation incurs a predefined unit cost.
   * 
   * @param plannerConstraint
   * @param unit cost
   * @return */
  public static CostFunction of(PlannerConstraint plannerConstraint, Scalar unit) {
    return new ConstraintViolationCost(plannerConstraint, unit);
  }

  /***************************************************/
  private final PlannerConstraint plannerConstraint;
  private final Scalar unit;
  private final Scalar unit_zero;

  private ConstraintViolationCost(PlannerConstraint plannerConstraint, Scalar unit) {
    this.plannerConstraint = Objects.requireNonNull(plannerConstraint);
    this.unit = unit;
    unit_zero = unit.zero();
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return plannerConstraint.isSatisfied(glcNode, trajectory, flow) //
        ? unit_zero
        : unit;
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return unit_zero;
  }
}
