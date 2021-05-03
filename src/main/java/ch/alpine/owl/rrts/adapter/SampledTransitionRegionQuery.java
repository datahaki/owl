// code by jph
package ch.alpine.owl.rrts.adapter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeCollector;
import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public class SampledTransitionRegionQuery implements TransitionRegionQuery, StateTimeCollector, Serializable {
  private final Region<Tensor> region;
  private final Scalar dt;

  public SampledTransitionRegionQuery(Region<Tensor> region, Scalar dt) {
    this.region = region;
    this.dt = dt;
  }

  @Override
  public boolean isDisjoint(Transition transition) {
    return transition.sampled(dt).stream() //
        .noneMatch(region::isMember);
  }

  @Override
  public Collection<StateTime> getMembers() {
    // if (trajectoryRegionQuery instanceof StateTimeCollector)
    // return ((StateTimeCollector) trajectoryRegionQuery).getMembers();
    return Collections.emptyList();
  }
}
