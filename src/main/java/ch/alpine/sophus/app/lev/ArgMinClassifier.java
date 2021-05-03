// code by jph
package ch.alpine.sophus.app.lev;

import java.util.Optional;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.red.ArgMin;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Clips;

/* package */ class ArgMinClassifier extends Classifier {
  /** @param labels */
  public ArgMinClassifier(Tensor labels) {
    super(Primitives.toIntArray(labels));
  }

  @Override // from Classification
  public ClassificationResult result(Tensor weights) {
    if (weights.length() != labels.length)
      throw TensorRuntimeException.of(weights);
    // ---
    int index = ArgMin.of(weights);
    int label = labels[index];
    Optional<Scalar> optional = IntStream.range(0, labels.length) //
        .filter(i -> label != labels[i]) //
        .mapToObj(weights::Get) //
        .reduce(Min::of);
    Scalar confidence = optional.isPresent() //
        ? Clips.unit().apply(RealScalar.ONE.subtract(weights.Get(index).divide(optional.get())))
        : RealScalar.ONE;
    return new ClassificationResult(label, confidence);
  }
}