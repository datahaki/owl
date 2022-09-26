// code by jph, gjoel
package ch.alpine.owl.rrts;

import java.util.Objects;

import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.rrts.core.BiRrtsPlanner;
import ch.alpine.owl.rrts.core.BidirectionalRrts;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.RrtsPlanner;
import ch.alpine.owl.rrts.core.TransitionCostFunction;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophus.crv.TransitionSpace;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public abstract class BiRrtsPlannerServer extends RrtsPlannerServer {
  private Tensor state = Tensors.empty();
  private Tensor goal = Tensors.empty();

  protected BiRrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel, //
      TransitionCostFunction costFunction) {
    super(transitionSpace, obstacleQuery, resolution, stateSpaceModel, costFunction);
  }

  @Override // from RrtsPlannerServer
  public void setState(StateTime stateTime) {
    super.setState(stateTime);
    state = stateTime.state();
  }

  @Override // from RrtsPlannerServer
  public void setGoal(Tensor goal) {
    this.goal = convertGoal(goal);
  }

  @Override // from RrtsPlannerServer
  protected RrtsPlannerProcess setupProcess(StateTime stateTime) {
    BidirectionalRrts rrts = new BidirectionalRrts(getTransitionSpace(), this::rrtsNodeCollection, obstacleQuery, costFunction,
        Objects.requireNonNull(stateTime).state(), goal);
    RrtsPlanner rrtsPlanner = new BiRrtsPlanner(rrts, spaceSampler(state));
    return new RrtsPlannerProcess(rrtsPlanner, rrts.getRoot());
  }

  protected abstract RrtsNodeCollection rrtsNodeCollection();

  protected abstract RandomSampleInterface spaceSampler(Tensor state);

  protected abstract Tensor convertGoal(Tensor goal);
}
