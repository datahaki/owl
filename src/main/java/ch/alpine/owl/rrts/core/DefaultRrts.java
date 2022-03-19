// code by jph
package ch.alpine.owl.rrts.core;

import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;

/** class implements the capability to build an rrts tree.
 * 
 * "Sampling-based algorithms for optimal motion planning"
 * by Sertac Karaman and Emilio Frazzoli
 * 
 * <p>the class does not require the concept of a sampler, or goal region.
 * 
 * @see DefaultRrtsPlanner */
public class DefaultRrts implements Rrts {
  private final TransitionSpace transitionSpace;
  private final RrtsNodeCollection nodeCollection;
  private final TransitionRegionQuery obstacleQuery;
  private final TransitionCostFunction transitionCostFunction;
  private int rewireCount = 0;

  public DefaultRrts( //
      TransitionSpace transitionSpace, //
      RrtsNodeCollection rrtsNodeCollection, //
      TransitionRegionQuery obstacleQuery, //
      TransitionCostFunction transitionCostFunction) {
    this.transitionSpace = transitionSpace;
    this.nodeCollection = rrtsNodeCollection;
    this.obstacleQuery = obstacleQuery;
    this.transitionCostFunction = transitionCostFunction;
  }

  @Override // from Rrts
  public Optional<RrtsNode> insertAsNode(Tensor state, int k_nearest) {
    return insertAsNode(state, k_nearest, false);
  }

  /* package */ Optional<RrtsNode> insertAsNode(Tensor state, int k_nearest, boolean ignoreCheck) {
    // the collision check available to class works on transitions, but not on states
    // that means no sanity collision check on state is carried out inside function insertAsNode
    int size = nodeCollection.size();
    if (size == 0) {
      RrtsNode rrtsNode = RrtsNode.createRoot(state, RealScalar.ZERO); // TODO OWL ALG units?
      nodeCollection.insert(rrtsNode);
      return Optional.of(rrtsNode);
    }
    if (ignoreCheck || isInsertPlausible(state)) { // TODO OWL ALG is this needed?
      k_nearest = Math.min(Math.max(1, k_nearest), size); // TODO OWL ALG not elegant
      Optional<RrtsNode> optional = connectAlongMinimumCost(state, k_nearest);
      if (optional.isPresent()) {
        RrtsNode rrtsNode = optional.orElseThrow();
        rewireAround(rrtsNode, k_nearest); // first: rewire
        nodeCollection.insert(rrtsNode); // second: insert to collection
        return Optional.of(rrtsNode);
      }
      System.err.println("Unable to connect " + state);
    }
    return Optional.empty();
  }

  // TODO OWL API probably remove
  private boolean isInsertPlausible(Tensor state) {
    RrtsNode nearest = nodeCollection.nearTo(state, 1).iterator().next().rrtsNode();
    return isCollisionFree(transitionSpace.connect(nearest.state(), state));
  }

  private Optional<RrtsNode> connectAlongMinimumCost(Tensor state, int k_nearest) {
    /* RrtsNode parent = null;
     * Scalar costFromRoot = null;
     * for (RrtsNode node : nodeCollection.nearTo(state, k_nearest)) {
     * Transition transition = transitionSpace.connect(node.state(), state);
     * Scalar cost = transitionCostFunction.cost(transition);
     * Scalar compare = node.costFromRoot().add(cost);
     * if (Objects.isNull(costFromRoot) || Scalars.lessThan(compare, costFromRoot))
     * if (isCollisionFree(transition)) {
     * parent = node;
     * costFromRoot = compare;
     * }
     * }
     * if (Objects.nonNull(parent))
     * return Optional.of(parent.connectTo(state, costFromRoot)); */
    final NavigableMap<Scalar, RrtsNode> updates = new TreeMap<>(Scalars::compare);
    nodeCollection.nearFrom(state, k_nearest).stream() //
        .forEach(rrtsNodeTransition -> {
          RrtsNode rrtsNode = rrtsNodeTransition.rrtsNode();
          Transition transition = rrtsNodeTransition.transition();
          Scalar cost = transitionCostFunction.cost(rrtsNode, transition);
          Scalar compare = rrtsNode.costFromRoot().add(cost);
          synchronized (updates) {
            if (updates.isEmpty() || Scalars.lessThan(compare, updates.firstKey()))
              if (isCollisionFree(transition))
                updates.put(compare, rrtsNode);
          }
        });
    if (!updates.isEmpty())
      return Optional.of(updates.firstEntry().getValue().connectTo(state, updates.firstKey()));
    return Optional.empty();
  }

  @Override // from Rrts
  public final void rewireAround(RrtsNode parent, int k_nearest) {
    for (RrtsNodeTransition rrtsNodeTransition : nodeCollection.nearFrom(parent.state(), k_nearest)) {
      RrtsNode child = rrtsNodeTransition.rrtsNode();
      Transition transition = rrtsNodeTransition.transition();
      Scalar costFromParent = transitionCostFunction.cost(parent, transition);
      if (Scalars.lessThan(parent.costFromRoot().add(costFromParent), child.costFromRoot()) && // reduce costs
          isCollisionFree(transition)) {
        parent.rewireTo(child, costFromParent); // , transitionCostFunction.influence());
        ++rewireCount;
      }
    }
  }

  @Override // from Rrts
  public int rewireCount() {
    return rewireCount;
  }

  // private helper function
  private boolean isCollisionFree(Transition transition) {
    return obstacleQuery.isDisjoint(transition);
  }

  /* package */ TransitionRegionQuery getObstacleQuery() {
    return obstacleQuery;
  }
}
