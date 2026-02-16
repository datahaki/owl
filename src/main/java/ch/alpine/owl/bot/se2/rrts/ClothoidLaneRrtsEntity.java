// code by jph, gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.awt.Graphics2D;
import java.util.Optional;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.adapter.FallbackControl;
import ch.alpine.owl.ani.api.AbstractRrtsEntity;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.bot.se2.glc.CarEntity;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.sophis.math.api.Region;
import ch.alpine.sophis.ts.ClothoidTransitionSpace;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;

/* package */ class ClothoidLaneRrtsEntity extends AbstractRrtsEntity {
  private static final Scalar DELAY_HINT = RealScalar.of(3);
  private static final StateSpaceModel STATE_SPACE_MODEL = Se2StateSpaceModel.INSTANCE;
  static final Tensor SHAPE = Tensors.matrixDouble( //
      new double[][] { //
          { .2, +.07 }, //
          { .25, +.0 }, //
          { .2, -.07 }, //
          { -.1, -.07 }, //
          { -.1, +.07 } //
      }).unmodifiable();

  // ---
  public ClothoidLaneRrtsEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, CoordinateBoundingBox box) {
    this(stateTime, transitionRegionQuery, box, false);
  }

  /** @param stateTime initial position of entity */
  public ClothoidLaneRrtsEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, CoordinateBoundingBox box, boolean greedy) {
    super( //
        new SimpleEpisodeIntegrator( //
            STATE_SPACE_MODEL, //
            EulerIntegrator.INSTANCE, //
            stateTime), //
        CarEntity.createPurePursuitControl(), //
        new LaneRrtsPlannerServer( //
            ClothoidTransitionSpace.ANALYTIC, //
            transitionRegionQuery, //
            Rational.of(1, 10), //
            STATE_SPACE_MODEL, //
            LengthCostFunction.INSTANCE, //
            greedy) {
          @Override
          protected RrtsNodeCollection rrtsNodeCollection() {
            return new Se2RrtsNodeCollection(getTransitionSpace(), box, 3);
          }

          @Override
          protected Tensor uBetween(StateTime orig, StateTime dest) {
            return CarRrtsFlow.uBetween(orig, dest);
          }
        });
    add(FallbackControl.of(Array.zeros(3)));
  }

  @Override // from AbstractRrtsEntity
  protected Tensor shape() {
    return SHAPE;
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return DELAY_HINT;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    LaneRrtsPlannerServer laneRrtsPlannerServer = (LaneRrtsPlannerServer) rrtsPlannerServer;
    Optional<Region<Tensor>> goalRegion = laneRrtsPlannerServer.goalRegion();
    if (goalRegion.isPresent())
      RegionRenders.draw(geometricLayer, graphics, goalRegion.get());
    super.render(geometricLayer, graphics);
  }

  public void setConical(boolean conical) {
    ((LaneRrtsPlannerServer) rrtsPlannerServer).setConical(conical);
  }
}
