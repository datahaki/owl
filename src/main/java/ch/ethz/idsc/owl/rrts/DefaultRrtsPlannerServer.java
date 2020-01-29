// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.core.DefaultRrts;
import ch.ethz.idsc.owl.rrts.core.DefaultRrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.GreedyRrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.Rrts;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsPlanner;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class DefaultRrtsPlannerServer extends RrtsPlannerServer {
  private Tensor state = Tensors.empty();
  private Tensor goal = Tensors.empty();
  /** collection of control points */
  private Collection<Tensor> greeds = Collections.emptyList();

  public DefaultRrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel, //
      TransitionCostFunction costFunction) {
    super(transitionSpace, obstacleQuery, resolution, stateSpaceModel, costFunction);
  }

  @Override // from RrtsPlannerServer
  public final void setState(StateTime stateTime) {
    super.setState(stateTime);
    state = stateTime.state();
  }

  @Override // from RrtsPlannerServer
  public final void setGoal(Tensor goal) {
    this.goal = goal;
  }

  @Override // from RrtsPlannerServer
  protected final RrtsPlannerProcess setupProcess(StateTime stateTime) {
    Rrts rrts = new DefaultRrts(getTransitionSpace(), rrtsNodeCollection(), obstacleQuery, costFunction);
    Optional<RrtsNode> optional = rrts.insertAsNode(stateTime.state(), 5);
    if (optional.isPresent()) {
      Collection<Tensor> greeds_ = greeds.stream() //
          .filter(point -> !optional.get().state().equals(point)) //
          .collect(Collectors.toList());
      RrtsPlanner rrtsPlanner = greeds_.isEmpty() //
          ? new DefaultRrtsPlanner(rrts, spaceSampler(state), goalSampler(goal)) //
          : new GreedyRrtsPlanner(rrts, spaceSampler(state), goalSampler(goal), greeds_).withGoal(goal);
      return new RrtsPlannerProcess(rrtsPlanner, optional.get());
    }
    return null;
  }

  /** @param greeds collection of control points */
  public final void setGreeds(Collection<Tensor> greeds) {
    this.greeds = Objects.requireNonNull(greeds);
  }

  /** @return new rrts node collection */
  protected abstract RrtsNodeCollection rrtsNodeCollection();

  protected abstract RandomSampleInterface spaceSampler(Tensor state);

  protected abstract RandomSampleInterface goalSampler(Tensor state);
}
