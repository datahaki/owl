// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Tensor;

/** determines membership for elements of type T 
 * 
 * common examples for type T are {@link Tensor} and {@link StateTime} */
@FunctionalInterface
public interface Region<T> {
  /** @param element
   * @return membership status of given element */
  boolean isMember(T element);
}
