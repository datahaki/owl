// code by bapaden and jph
package ch.alpine.owl.math.state;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Tensor;

/** StateTimeRegion, which is independent of time.
 * membership is determined in state space regardless of time.
 * membership is extended indefinitely along the time-axis */
public final class TimeInvariantRegion implements Region<StateTime>, Serializable {
  private final Region<Tensor> region;

  public TimeInvariantRegion(Region<Tensor> region) {
    this.region = Objects.requireNonNull(region);
  }

  /** @param StateTime of point to check
   * @return true if stateTime is member/part of/inside region */
  @Override
  public boolean test(StateTime stateTime) {
    return region.test(stateTime.state());
  }
}
