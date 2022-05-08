// code by gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.util.Map;
import java.util.function.Consumer;

import ch.alpine.owl.ani.adapter.FallbackControl;
import ch.alpine.owl.ani.api.AbstractRrtsEntity;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.bot.se2.glc.CarEntity;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophus.clt.ClothoidTransitionSpace;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;

/** variant of {@link ClothoidLaneRrtsEntity} intended for simulation
 * TODO OWL API perhaps create intermediate class with common code to derive from */
/* package */ class ClothoidLaneEntity extends AbstractRrtsEntity {
  private static final StateSpaceModel STATE_SPACE_MODEL = Se2StateSpaceModel.INSTANCE;
  private final Scalar delayHint;

  /** @param stateTime initial position of entity */
  /* package */ ClothoidLaneEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, CoordinateBoundingBox box, boolean greedy,
      Scalar delayHint, Consumer<Map<Double, Scalar>> process, Consumer<RrtsNode> processFirst, Consumer<RrtsNode> processLast) {
    super( //
        new SimpleEpisodeIntegrator( //
            STATE_SPACE_MODEL, //
            EulerIntegrator.INSTANCE, //
            stateTime), //
        CarEntity.createPurePursuitControl(), //
        new LaneRrtsPlannerServer( //
            ClothoidTransitionSpace.ANALYTIC, //
            transitionRegionQuery, //
            RationalScalar.of(1, 10), //
            STATE_SPACE_MODEL, //
            LengthCostFunction.INSTANCE, //
            greedy) {
          @Override // from DefaultRrtsPlannerServer
          protected RrtsNodeCollection rrtsNodeCollection() {
            return new Se2RrtsNodeCollection(getTransitionSpace(), box, 3);
          }

          @Override // from RrtsPlannerServer
          protected Tensor uBetween(StateTime orig, StateTime dest) {
            return CarRrtsFlow.uBetween(orig, dest);
          }

          @Override // from ObservingExpandInterface
          public boolean isObserving() {
            return true;
          }

          @Override // from ObservingExpandInterface
          public void process(Map<Double, Scalar> observations) {
            process.accept(observations);
            super.process(observations);
          }

          @Override // from ObservingExpandInterface
          public void processFirst(RrtsNode first) {
            processFirst.accept(first);
          }

          @Override // from ObservingExpandInterface
          public void processLast(RrtsNode last) {
            processLast.accept(last);
          }
        });
    add(FallbackControl.of(Array.zeros(3)));
    this.delayHint = delayHint;
  }

  @Override // from AbstractRrtsEntity
  protected Tensor shape() {
    return null;
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return delayHint;
  }
}