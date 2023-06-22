// code by astoll
package ch.alpine.owl.bot.ap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.sca.N;

/* package */ class ApFlows implements FlowsInterface, Serializable {
  /** @param aoa_max unitless
   * @param thrusts vector with unit [N]
   * @return new ApFlows instance */
  public static FlowsInterface of(Scalar aoa_max, Tensor thrusts) {
    return new ApFlows(aoa_max, thrusts);
  }

  // ---
  private final Scalar aoa_max;
  private final Tensor thrusts;

  private ApFlows(Scalar aoa_max, Tensor thrusts) {
    this.aoa_max = aoa_max;
    this.thrusts = VectorQ.require(thrusts);
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    Collection<Tensor> collection = new ArrayList<>();
    for (Tensor thrust : thrusts)
      for (Tensor aoa : Subdivide.of(aoa_max.zero(), aoa_max, resolution))
        collection.add(Tensors.of(thrust, aoa).map(N.DOUBLE));
    return collection;
  }
}
