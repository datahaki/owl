// code by gjoel, jph
package ch.alpine.owl.rrts.adapter;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.opt.nd.NdCenterInterface;

public interface NdType {
  /** @param tensor
   * @return nd center that measures distance to given tensor */
  NdCenterInterface ndCenterTo(Tensor tensor);

  /** @param tensor
   * @return nd center that measures distance from given tensor */
  NdCenterInterface ndCenterFrom(Tensor tensor);
}
