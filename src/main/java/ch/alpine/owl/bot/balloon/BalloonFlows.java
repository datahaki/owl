// code by astoll
package ch.alpine.owl.bot.balloon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.sca.N;

/* package */ class BalloonFlows implements FlowsInterface, Serializable {
  /** @param u_max with units [K * s^-1]
   * @return new ApFlows instance */
  public static FlowsInterface of(Scalar u_max) {
    return new BalloonFlows(u_max);
  }

  // ---
  private final Scalar u_max;

  private BalloonFlows(Scalar u_max) {
    this.u_max = Objects.requireNonNull(u_max);
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    Collection<Tensor> collection = new ArrayList<>();
    for (Tensor u : Subdivide.of(u_max.negate(), u_max, 1 + resolution))
      collection.add(Tensors.of(u).maps(N.DOUBLE));
    return collection;
  }
}
