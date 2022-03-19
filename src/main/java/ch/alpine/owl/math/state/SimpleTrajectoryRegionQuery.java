// code by jph
package ch.alpine.owl.math.state;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.Tensor;

/** simple wrapper for obstacle and goal queries
 * implementation is used throughout the repository */
public class SimpleTrajectoryRegionQuery implements TrajectoryRegionQuery, Serializable {
  /** @param region that is queried with tensor = StateTime::state
   * @return
   * @throws Exception if given region is null */
  public static TrajectoryRegionQuery timeInvariant(Region<Tensor> region) {
    return new SimpleTrajectoryRegionQuery(new TimeInvariantRegion(region));
  }

  // ---
  protected final Region<StateTime> region;

  /** @param region non-null */
  public SimpleTrajectoryRegionQuery(Region<StateTime> region) {
    this.region = Objects.requireNonNull(region);
  }

  @Override // from TrajectoryRegionQuery
  public final Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectory.stream().filter(this::test).findFirst();
  }

  @Override // from Region
  public boolean test(StateTime stateTime) {
    return region.test(stateTime);
  }
}
