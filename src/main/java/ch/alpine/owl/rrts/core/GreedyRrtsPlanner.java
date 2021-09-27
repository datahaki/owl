// code by jph
package ch.alpine.owl.rrts.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;

import ch.alpine.owl.data.tree.NodeCostComparator;
import ch.alpine.owl.data.tree.StateCostNode;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class GreedyRrtsPlanner extends DefaultRrtsPlanner {
  private final Collection<Tensor> greeds;
  private Tensor goal = Tensors.empty();

  /** @param rrts with root already inserted
   * @param spaceSample
   * @param goalSample generates samples in goal region */
  public GreedyRrtsPlanner(Rrts rrts, RandomSampleInterface spaceSample, RandomSampleInterface goalSample, Collection<Tensor> greeds) {
    this(rrts, spaceSample, goalSample, NodeCostComparator.INSTANCE, greeds);
  }

  /** @param rrts with root already inserted
   * @param spaceSample
   * @param goalSample generates samples in goal region
   * @param nodeComparator */
  public GreedyRrtsPlanner(Rrts rrts, RandomSampleInterface spaceSample, RandomSampleInterface goalSample, Comparator<StateCostNode> nodeComparator, //
      Collection<Tensor> greeds) {
    super(rrts, spaceSample, goalSample, nodeComparator);
    this.greeds = new ArrayList<>(greeds);
  }

  public GreedyRrtsPlanner withGoal(Tensor goal) {
    this.goal = goal;
    return this;
  }

  @Override // from ExpandInterface
  public void expand(RrtsNode node) { // node is not used, instead new random sample
    boolean satisfied = false;
    Iterator<Tensor> iterator = greeds.iterator();
    while (iterator.hasNext()) {
      Tensor greed = iterator.next();
      Optional<RrtsNode> optional = rrts.insertAsNode(greed, K_NEAREST);
      if (optional.isPresent()) {
        if (optional.orElseThrow().state().equals(goal))
          queue.add(optional.orElseThrow());
        iterator.remove();
        satisfied = true;
        break;
      }
    }
    if (!satisfied)
      super.expand(node);
  }
}
