// code by jph
package ch.alpine.owl.math.region;

/** determines membership for elements of type T */
@FunctionalInterface
public interface Region<T> {
  /** @param element
   * @return membership status of given element */
  boolean isMember(T element);
}
