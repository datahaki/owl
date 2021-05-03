// code by jph
package ch.alpine.owl.glc.rl;

import java.util.Map;
import java.util.Map.Entry;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.red.Entrywise;

/* package */ class DQMInspection {
  private final Tensor min;
  private final Tensor max;
  private final Tensor count;

  public DQMInspection(Map<Tensor, RLDomainQueue> rlDomainQueueMap) {
    min = rlDomainQueueMap.keySet().stream().reduce(Entrywise.min()).get();
    max = rlDomainQueueMap.keySet().stream().reduce(Entrywise.max()).get();
    Tensor width = max.subtract(min).map(RealScalar.ONE::add);
    if (!ExactTensorQ.of(width))
      throw TensorRuntimeException.of(min, max, width);
    count = Array.zeros(Primitives.toListInteger(width));
    for (Entry<Tensor, RLDomainQueue> entry : rlDomainQueueMap.entrySet()) {
      Tensor key = entry.getKey();
      RLDomainQueue value = entry.getValue();
      Integer[] array = Primitives.toListInteger(key.subtract(min)).toArray(new Integer[min.length()]);
      count.set(RealScalar.of(value.size()), array);
    }
  }

  public Tensor getCount() {
    return count;
  }
}
