// code by jph
package ch.alpine.owl.bot.delta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.rot.CirclePoints;

record DeltaFlows(Scalar amp) implements FlowsInterface, Serializable {
  @Override // from FlowsInterface
  public Collection<Tensor> getFlows(int resolution) {
    Collection<Tensor> collection = new ArrayList<>();
    for (Tensor u : CirclePoints.of(resolution))
      collection.add(u.multiply(amp));
    return collection;
  }
}
