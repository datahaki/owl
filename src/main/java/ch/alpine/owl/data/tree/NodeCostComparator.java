// code by bapaden and jph
package ch.alpine.owl.data.tree;

import java.util.Comparator;

import ch.alpine.tensor.Scalars;

/** compare two nodes based on {@link StateCostNode#costFromRoot()}
 * used in rrts and glc */
public enum NodeCostComparator implements Comparator<StateCostNode> {
  INSTANCE;

  @Override
  public int compare(StateCostNode o1, StateCostNode o2) {
    return Scalars.compare(o1.costFromRoot(), o2.costFromRoot());
  }
}
