// code by jph
package ch.alpine.owl.rrts.core;

import ch.alpine.sophus.crv.Transition;

@FunctionalInterface
public interface TransitionRegionQuery {
  /** @param transition
   * @return true, if the transition does not intersect this region */
  boolean isDisjoint(Transition transition);
}
