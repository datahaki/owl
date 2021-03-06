// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.List;
import java.util.Optional;

import ch.alpine.owl.math.pursuit.PurePursuit;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.sophus.hs.r2.Se2Bijection;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.Sign;

/** pure pursuit control */
/* package */ class PurePursuitControl extends LookAheadControl {
  /** @param lookAhead distance
   * @param maxTurningRate */
  public PurePursuitControl(Scalar lookAhead, Scalar maxTurningRate) {
    super(lookAhead, maxTurningRate);
  }

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Tensor u = trailAhead.get(0).getFlow().get();
    Scalar speed = u.Get(0);
    Tensor state = tail.state();
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(state).inverse();
    Tensor beacons = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(tensorUnaryOperator));
    if (Sign.isNegative(speed))
      beacons.set(Scalar::negate, Tensor.ALL, 0);
    PurePursuit purePursuit = PurePursuit.fromTrajectory(beacons, lookAhead);
    if (purePursuit.firstRatio().isPresent()) {
      Scalar ratio = purePursuit.firstRatio().get();
      if (clip.isInside(ratio)) {
        targetLocal = purePursuit.lookAhead().get(); // ratio isPresent implies lookAhead isPresent
        return Optional.of(Se2CarFlows.singleton(speed, ratio));
      }
    }
    targetLocal = null;
    return Optional.empty();
  }
}
