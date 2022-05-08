// code by jph
package ch.alpine.owl.rrts.core;

import ch.alpine.sophus.api.Transition;

public class RrtsNodeTransition {
  private final RrtsNode rrtsNode;
  private final Transition transition;

  public RrtsNodeTransition(RrtsNode rrtsNode, Transition transition) {
    this.rrtsNode = rrtsNode;
    this.transition = transition;
  }

  public RrtsNode rrtsNode() {
    return rrtsNode;
  }

  public Transition transition() {
    return transition;
  }
}
