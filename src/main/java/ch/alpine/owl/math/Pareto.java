// code by ynager
package ch.alpine.owl.math;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

public enum Pareto {
  ;
  /** @param first vector
   * @param second vector
   * @return true only all entries in second surpass the corresponding entry in first
   * @throws Exception if input parameters are not vectors of the same length */
  public static boolean isDominated(Tensor first, Tensor second) {
    return first.subtract(second).stream() //
        .map(Scalar.class::cast) //
        .allMatch(Sign::isNegative);
  }
}
