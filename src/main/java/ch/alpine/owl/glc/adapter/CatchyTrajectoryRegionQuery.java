// code by bapaden and jph
package ch.alpine.owl.glc.adapter;

import java.util.Collection;

import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeCollector;
import ch.alpine.owl.math.state.StateTimeRegionCallback;
import ch.alpine.owl.math.state.TimeDependentRegion;
import ch.alpine.owl.math.state.TimeInvariantRegion;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Tensor;

/** wrapper for obstacle and goal queries */
public class CatchyTrajectoryRegionQuery extends SimpleTrajectoryRegionQuery implements StateTimeCollector {
  /** @param region that is queried with tensor = StateTime::state
   * @return */
  public static TrajectoryRegionQuery timeInvariant(Region<Tensor> region) {
    return new CatchyTrajectoryRegionQuery(new TimeInvariantRegion(region));
  }

  /** @param region that is queried with tensor = StateTime::joined
   * @return */
  public static TrajectoryRegionQuery timeDependent(Region<Tensor> region) {
    return new CatchyTrajectoryRegionQuery(new TimeDependentRegion(region));
  }

  public static TrajectoryRegionQuery wrap(Region<StateTime> region) {
    return new CatchyTrajectoryRegionQuery(region);
  }

  // ---
  private final StateTimeRegionCallback stateTimeRegionCallback;

  /** @param region */
  private CatchyTrajectoryRegionQuery(Region<StateTime> region) {
    super(region);
    this.stateTimeRegionCallback = new SparseStateTimeRegionMembers();
  }

  @Override // from TrajectoryRegionQuery
  public final boolean test(StateTime stateTime) {
    boolean isMember = region.test(stateTime);
    if (isMember)
      stateTimeRegionCallback.notify_isMember(stateTime);
    return isMember;
  }

  public StateTimeRegionCallback getStateTimeRegionCallback() {
    return stateTimeRegionCallback;
  }

  /** Region members, which were found in Region
   * for GUI as only 1 State is allowed in 1 Raster (for sparsity)
   * 
   * @return Collection<StateTime> the members of the sparse raster */
  @Override // from StateTimeCollector
  public Collection<StateTime> getMembers() {
    return ((StateTimeCollector) getStateTimeRegionCallback()).getMembers();
  }
}
