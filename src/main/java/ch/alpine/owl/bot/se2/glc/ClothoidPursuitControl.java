// code by gjoel
package ch.alpine.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.List;
import java.util.Optional;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.ani.adapter.StateTrajectoryControl;
import ch.alpine.owl.bot.se2.Se2Wrap;
import ch.alpine.owl.math.pursuit.ArgMinVariable;
import ch.alpine.owl.math.pursuit.ClothoidPursuit;
import ch.alpine.owl.math.pursuit.ClothoidPursuits;
import ch.alpine.owl.math.pursuit.PursuitInterface;
import ch.alpine.owl.math.pursuit.TrajectoryEntryFinder;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.sophus.lie.se2.Se2GroupElement;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

/* package */ class ClothoidPursuitControl extends StateTrajectoryControl implements TrajectoryTargetRender {
  private final static int MAX_LEVEL = 20;
  private final static int REFINEMENT = 2;
  // ---
  private final TrajectoryEntryFinder entryFinder;
  // ---
  private Clip ratioClip;
  private Tensor curve; // for visualization

  /** @param entryFinder strategy
   * @param maxTurningRate limits = {-maxTurningRate, +maxTurningRate} */
  public ClothoidPursuitControl(TrajectoryEntryFinder entryFinder, Scalar maxTurningRate) {
    this.entryFinder = entryFinder;
    setRatioLimit(Clips.absolute(maxTurningRate));
  }

  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return Vector2NormSquared.of(Se2Wrap.INSTANCE.difference(x, y));
  }

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Scalar speed = trailAhead.get(0).getFlow().orElseThrow().Get(0);
    boolean inReverse = Sign.isNegative(speed);
    Tensor state = tail.state();
    TensorUnaryOperator tensorUnaryOperator = new Se2GroupElement(state).inverse()::combine;
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(tensorUnaryOperator));
    if (inReverse)
      ClothoidControlHelper.mirrorAndReverse(beacons);
    // ---
    TensorScalarFunction costMapping = new ClothoidLengthCostFunction(ratioClip::isInside);
    Scalar var = ArgMinVariable.using(entryFinder, costMapping, MAX_LEVEL).apply(beacons);
    Optional<Tensor> lookAhead = entryFinder.on(beacons).apply(var).point();
    if (lookAhead.isPresent()) {
      Tensor xya = lookAhead.orElseThrow();
      PursuitInterface pursuitInterface = ClothoidPursuit.of(xya);
      curve = ClothoidPursuits.curve(xya, REFINEMENT);
      if (inReverse)
        ClothoidControlHelper.mirrorAndReverse(curve);
      return Optional.of(Se2CarFlows.singleton(speed, pursuitInterface.firstRatio().orElseThrow()));
    }
    curve = null;
    // System.err.println("no compliant strategy found!");
    return Optional.empty();
  }

  /** @param ratioClip on turning ratio depending on state and speed */
  public void setRatioLimit(Clip ratioClip) {
    this.ratioClip = ratioClip;
  }

  @Override // from TrajectoryTargetRender
  public Optional<Shape> toTarget(GeometricLayer geometricLayer) {
    return Optional.ofNullable(curve).map(geometricLayer::toPath2D);
  }
}
