// code by ynager
package ch.alpine.owl.glc.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Tensor;

/** combines multiple PlannerConstraints
 * 
 * @see GoalAdapter */
public class MultiConstraintAdapter implements PlannerConstraint {
  /** @param plannerConstraints non-null
   * @return */
  public static PlannerConstraint of(Collection<PlannerConstraint> plannerConstraints) {
    return new MultiConstraintAdapter(plannerConstraints);
  }

  // ---
  private final Collection<PlannerConstraint> plannerConstraints;

  private MultiConstraintAdapter(Collection<PlannerConstraint> plannerConstraints) {
    this.plannerConstraints = Objects.requireNonNull(plannerConstraints);
  }

  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return plannerConstraints.stream() //
        .allMatch(constraint -> constraint.isSatisfied(glcNode, trajectory, flow));
  }
}
