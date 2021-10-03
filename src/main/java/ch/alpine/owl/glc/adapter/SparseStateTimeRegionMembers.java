// code by jph
package ch.alpine.owl.glc.adapter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import ch.alpine.owl.math.LinearRasterMap;
import ch.alpine.owl.math.RasterMap;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeRegionCallback;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;

/** distinguishes the first 2 coordinates of StateTime::state */
class SparseStateTimeRegionMembers implements StateTimeRegionCallback, Serializable {
  /** magic constants of scale are not universal but are suitable for most examples */
  private final RasterMap<StateTime> rasterMap = new LinearRasterMap<>(Tensors.vector(10, 10));

  @Override // from StateTimeRegionCallback
  public void notify_isMember(StateTime stateTime) {
    Tensor x = stateTime.state();
    Tensor key = 1 == x.length() //
        ? Append.of(x, RealScalar.ZERO)
        : Extract2D.FUNCTION.apply(x);
    rasterMap.put(key, stateTime);
  }

  @Override // from StateTimeCollector
  public Collection<StateTime> getMembers() {
    return Collections.unmodifiableCollection(rasterMap.values());
  }
}
