// code by jph
package ch.alpine.sophus.app.lev;

import java.util.Map;
import java.util.Map.Entry;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.red.Tally;

/* package */ class KNearestClassifier extends Classifier {
  private final int k;

  /** @param labels
   * @param k */
  public KNearestClassifier(Tensor labels, int k) {
    super(Primitives.toIntArray(labels));
    this.k = k;
  }

  @Override // from Classification
  public ClassificationResult result(Tensor weights) {
    if (weights.length() != labels.length)
      throw TensorRuntimeException.of(weights);
    // ---
    // TODO this is not finished yet!
    Map<Tensor, Long> map = Tally.of(Ordering.INCREASING.stream(weights) //
        .limit(k) //
        .map(i -> labels[i]) //
        .map(RealScalar::of));
    Scalar lab = null;
    int cmp = 0;
    for (Entry<Tensor, Long> entry : map.entrySet())
      if (cmp < entry.getValue())
        lab = (Scalar) entry.getKey();
    return new ClassificationResult(lab.number().intValue(), RealScalar.of(0.5));
  }
}