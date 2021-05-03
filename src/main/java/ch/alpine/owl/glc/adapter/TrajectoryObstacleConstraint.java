// code by jph
package ch.alpine.owl.glc.adapter;

import java.io.Serializable;
import java.util.List;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.tensor.Tensor;

/** class that wraps a given {@link TrajectoryRegionQuery} to an obstacle constraint.
 * non-empty intersection of the trajectory with the region represents a constraint
 * violation. */
public class TrajectoryObstacleConstraint implements PlannerConstraint, Serializable {
  private final TrajectoryRegionQuery trajectoryRegionQuery;

  public TrajectoryObstacleConstraint(TrajectoryRegionQuery trajectoryRegionQuery) {
    this.trajectoryRegionQuery = trajectoryRegionQuery;
  }

  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return !trajectoryRegionQuery.firstMember(trajectory).isPresent();
  }

  public TrajectoryRegionQuery getTrajectoryRegionQuery() {
    return trajectoryRegionQuery;
  }
}
