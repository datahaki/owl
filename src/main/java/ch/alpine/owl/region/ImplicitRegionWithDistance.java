// code by jph
package ch.alpine.owl.region;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Ramp;

public abstract class ImplicitRegionWithDistance extends ImplicitFunctionRegion //
    implements RegionWithDistance<Tensor> {
  @Override // from DistanceFunction<Tensor>
  public final Scalar distance(Tensor tensor) {
    return Ramp.FUNCTION.apply(signedDistance(tensor));
  }
}
