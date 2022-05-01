// code by jph
package ch.alpine.sophus.demo.lev;

import java.io.Serializable;
import java.util.Arrays;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.ExactTensorQ;

public abstract class Classifier implements Classification, Serializable {
  /** @param labels
   * @return */
  public static Classification argMin(Tensor labels) {
    return new ArgMinClassifier(ExactTensorQ.require(labels));
  }

  /** @param labels
   * @return */
  public static Classification argMax(Tensor labels) {
    return new ArgMaxClassifier(ExactTensorQ.require(labels));
  }

  /** @param labels
   * @return */
  public static Classification accMax(Tensor labels) {
    return new AccMaxClassifier(ExactTensorQ.require(labels));
  }

  // ---
  protected final int[] labels;
  protected final int size;

  /** @param labels */
  protected Classifier(int[] labels) {
    this.labels = labels;
    size = Arrays.stream(labels).reduce(Math::max).orElse(0) + 1;
  }
}