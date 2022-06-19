// code by jph
package ch.alpine.owl.math.state;

import java.io.Serializable;

import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Tensor;

/** StateTimeRegion that depends on time */
public final class TimeDependentRegion implements Region<StateTime>, Serializable {
  private final Region<Tensor> region;

  /** @param region */
  public TimeDependentRegion(Region<Tensor> region) {
    this.region = region;
  }

  /** @param StateTime of point to check
   * @return true if stateTime is member/part of/inside region */
  @Override
  public boolean test(StateTime stateTime) {
    return region.test(stateTime.joined());
  }
}
