// code by jph
package ch.alpine.sophus.app.lev;

import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface Classification {
  /** @param weights
   * @return */
  ClassificationResult result(Tensor weights);
}
