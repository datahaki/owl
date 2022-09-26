// code by jph, gjoel
package ch.alpine.owl.rrts;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.rrts.core.DefaultRrts;
import ch.alpine.owl.rrts.core.DefaultRrtsPlanner;
import ch.alpine.owl.rrts.core.GreedyRrtsPlanner;
import ch.alpine.owl.rrts.core.Rrts;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.RrtsPlanner;
import ch.alpine.owl.rrts.core.TransitionCostFunction;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophus.crv.TransitionSpace;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public abstract class DefaultRrtsPlannerServer extends RrtsPlannerServer {
  private Tensor state = Tensors.empty();
  private Tensor goal = Tensors.empty();
  /** collection of control points */
  private Collection<Tensor> greeds = Collections.emptyList();

  protected DefaultRrtsPlannerServer( //
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
          .filter(point -> !optional.orElseThrow().state().equals(point)) //
          .collect(Collectors.toList());
      RrtsPlanner rrtsPlanner = greeds_.isEmpty() //
          ? new DefaultRrtsPlanner(rrts, spaceSampler(state), goalSampler(goal)) //
          : new GreedyRrtsPlanner(rrts, spaceSampler(state), goalSampler(goal), greeds_).withGoal(goal);
      return new RrtsPlannerProcess(rrtsPlanner, optional.orElseThrow());
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
