// code by jph
package ch.alpine.owl.math;

import ch.alpine.tensor.Scalar;

/** https://en.wikipedia.org/wiki/Signed_distance_function */
@FunctionalInterface
public interface SignedDistanceFunction<T> {
  /** @param element
   * @return signed distance to given element */
  Scalar signedDistance(T element);
}
