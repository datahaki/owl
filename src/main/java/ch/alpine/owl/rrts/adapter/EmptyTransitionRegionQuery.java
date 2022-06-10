// code by jph
package ch.alpine.owl.rrts.adapter;

import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophus.crv.Transition;

public enum EmptyTransitionRegionQuery implements TransitionRegionQuery {
  INSTANCE;

  // ---
  @Override // from TransitionRegionQuery
  public boolean isDisjoint(Transition transition) {
    return true;
  }
}
