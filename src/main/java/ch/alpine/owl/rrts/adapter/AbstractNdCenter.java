// code by jph, gjoel
package ch.alpine.owl.rrts.adapter;

import java.io.Serializable;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.NdCenterInterface;

/* package */ abstract class AbstractNdCenter implements NdCenterInterface, Serializable {
  private final Tensor center;

  public AbstractNdCenter(Tensor center) {
    this.center = center.copy().unmodifiable();
  }

  @Override // from NdCenterInterface
  public final Tensor center() {
    return center;
  }
}
