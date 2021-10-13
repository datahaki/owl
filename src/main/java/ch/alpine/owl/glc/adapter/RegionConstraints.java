// code by jph
package ch.alpine.owl.glc.adapter;

import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TimeDependentRegion;
import ch.alpine.owl.math.state.TimeInvariantRegion;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.Tensor;

/** members in the given regions are considered obstacles */
public enum RegionConstraints {
  ;
  /** @param region that is queried with tensor = StateTime::state
   * @return planner constraint that threats members in the region as obstacles */
  public static PlannerConstraint timeInvariant(Region<Tensor> region) {
    return new TrajectoryObstacleConstraint( //
        new SimpleTrajectoryRegionQuery(new TimeInvariantRegion(region)));
  }

  /** @param region that is queried with tensor = StateTime::joined
   * @return planner constraint that threats members in the region as obstacles */
  public static PlannerConstraint timeDependent(Region<Tensor> region) {
    return new TrajectoryObstacleConstraint( //
        new SimpleTrajectoryRegionQuery(new TimeDependentRegion(region)));
  }

  public static PlannerConstraint stateTime(Region<StateTime> region) {
    return new TrajectoryObstacleConstraint( //
        new SimpleTrajectoryRegionQuery(region));
  }
}
