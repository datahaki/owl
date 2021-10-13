// code by jph
package ch.alpine.sophus.demo.lev;

import ch.alpine.tensor.Tensor;

@FunctionalInterface
public interface Classification {
  /** @param weights
   * @return */
  ClassificationResult result(Tensor weights);
}
