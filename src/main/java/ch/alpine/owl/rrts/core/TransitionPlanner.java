// code by gjoel
package ch.alpine.owl.rrts.core;

import ch.alpine.owl.data.tree.TreePlanner;

public interface TransitionPlanner extends TreePlanner<RrtsNode> {
  void checkConsistency();
}
