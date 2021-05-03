// code by jph
package ch.alpine.owl.math;

import ch.alpine.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/DistanceFunction.html">DistanceFunction</a> */
@FunctionalInterface
public interface DistanceFunction<T> {
  /** @param element
   * @return non-negative distance to given element */
  Scalar distance(T element);
}
