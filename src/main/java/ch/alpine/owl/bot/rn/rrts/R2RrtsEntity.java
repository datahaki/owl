// code by jph, gjoel
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.owl.ani.adapter.FallbackControl;
import ch.alpine.owl.ani.api.AbstractRrtsEntity;
import ch.alpine.owl.bot.rn.RnTransitionSpace;
import ch.alpine.owl.bot.rn.glc.R2TrajectoryControl;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.rrts.DefaultRrtsPlannerServer;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.ConstantRandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.Box;

// TODO the redundancy in R2****Entity shows that re-factoring is needed!
/* package */ class R2RrtsEntity extends AbstractRrtsEntity {
  /** preserve 0.5[s] of the former trajectory */
  private static final Scalar DELAY_HINT = RealScalar.of(0.5);
  private static final StateSpaceModel STATE_SPACE_MODEL = SingleIntegratorStateSpaceModel.INSTANCE;
  static final Tensor SHAPE = Tensors.fromString("{{0, 0.1}, {0.1, 0}, {0, -0.1}, {-0.1, 0}}").unmodifiable();

  /** @param stateTime initial position of entity
   * @param transitionRegionQuery
   * @param box */
  public R2RrtsEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, Box box) {
    super( //
        new SimpleEpisodeIntegrator( //
            STATE_SPACE_MODEL, //
            EulerIntegrator.INSTANCE, //
            stateTime), //
        new R2TrajectoryControl(), //
        new DefaultRrtsPlannerServer( //
            RnTransitionSpace.INSTANCE, //
            transitionRegionQuery, //
            RationalScalar.of(1, 10), //
            STATE_SPACE_MODEL, //
            LengthCostFunction.INSTANCE) {
          @Override
          protected RrtsNodeCollection rrtsNodeCollection() {
            return new RnRrtsNodeCollection(box);
          }

          @Override
          protected RandomSampleInterface spaceSampler(Tensor state) {
            return BoxRandomSample.of(box);
          }

          @Override
          protected RandomSampleInterface goalSampler(Tensor goal) {
            return new ConstantRandomSample(Extract2D.FUNCTION.apply(goal));
          }

          @Override
          protected Tensor uBetween(StateTime orig, StateTime dest) {
            return RnRrtsFlow.uBetween(orig, dest);
          }
        });
    add(FallbackControl.of(Array.zeros(2)));
  }

  @Override // from AbstractRrtsEntity
  protected Tensor shape() {
    return SHAPE;
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return DELAY_HINT;
  }
}
