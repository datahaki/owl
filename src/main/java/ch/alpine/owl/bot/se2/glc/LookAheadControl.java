// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.awt.Shape;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Clips;

/* package */ abstract class LookAheadControl extends Se2TrajectoryControl {
  // ---
  final Scalar lookAhead;
  /** for drawing only */
  Tensor targetLocal = null;

  public LookAheadControl(Scalar lookAhead, Scalar maxTurningRate) {
    super(Clips.absolute(maxTurningRate));
    this.lookAhead = lookAhead;
  }

  @Override // from TrajectoryTargetRender
  public final Optional<Shape> toTarget(GeometricLayer geometricLayer) {
    Tensor _targetLocal = targetLocal; // copy reference
    if (Objects.nonNull(_targetLocal))
      return Optional.of(geometricLayer.toLine2D(_targetLocal));
    return Optional.empty();
  }
}
