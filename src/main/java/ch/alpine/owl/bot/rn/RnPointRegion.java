// code by jph
package ch.alpine.owl.bot.rn;

import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2Norm;

public class RnPointRegion implements RegionWithDistance<Tensor> {
  private final Tensor tensor;

  public RnPointRegion(Tensor tensor) {
    this.tensor = tensor;
  }

  @Override // from RegionWithDistance
  public boolean isMember(Tensor element) {
    return element.equals(tensor);
  }

  @Override // from RegionWithDistance
  public Scalar distance(Tensor element) {
    return Vector2Norm.between(tensor, element);
  }
}
