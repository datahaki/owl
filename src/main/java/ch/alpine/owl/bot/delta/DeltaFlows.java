// code by jph
package ch.alpine.owl.bot.delta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.r2.CirclePoints;

/* package */ class DeltaFlows implements FlowsInterface, Serializable {
  // ---
  private final Scalar amp;

  public DeltaFlows(Scalar amp) {
    this.amp = amp;
  }

  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    Collection<Tensor> collection = new ArrayList<>();
    for (Tensor u : CirclePoints.of(resolution))
      collection.add(u.multiply(amp));
    return collection;
  }
}
