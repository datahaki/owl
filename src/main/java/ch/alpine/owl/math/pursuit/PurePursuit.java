// code by jph
package ch.alpine.owl.math.pursuit;

import java.util.Optional;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sin;

public class PurePursuit implements PursuitInterface {
  /** @param lookAhead {x, y, ...} where x is positive
   * @return rate with interpretation in [m^-1], or empty if the first coordinate
   * of the look ahead beacon is non-positive
   * @throws Exception if lookAhead has insufficient length */
  public static Optional<Scalar> ratioPositiveX(Tensor lookAhead) {
    Scalar x = lookAhead.Get(0);
    if (Sign.isPositive(x)) {
      Scalar angle = ArcTan.of(x, lookAhead.Get(1));
      // in the formula below, 2*angle == angle+angle is not a magic constant
      // but has an exact geometric interpretation
      return Optional.of(Sin.FUNCTION.apply(angle.add(angle)).divide(x));
    }
    return Optional.empty();
  }

  /** @param lookAhead {x, y, ...} where x is negative
   * @return rate with interpretation in [m^-1], or empty if the first coordinate
   * of the look ahead beacon is non-positive
   * @throws Exception if lookAhead has insufficient length */
  public static Optional<Scalar> ratioNegativeX(Tensor lookAhead) {
    Tensor target = lookAhead.copy();
    target.set(Scalar::negate, 0);
    return ratioPositiveX(target);
  }

  /** function also works with units, for instance if point coordinates and
   * distance are provided with "m" the ratio will be a quantity with unit "m^-1".
   * 
   * @param tensor of waypoints {{x1, y1}, {x2, y2}, ...}
   * @param distance to look ahead
   * @return */
  public static PurePursuit fromTrajectory(Tensor tensor, Scalar distance) {
    return new PurePursuit(new SphereCurveIntersection(distance).string(tensor));
  }

  // ---
  private final Optional<Tensor> lookAhead;
  private final Optional<Scalar> ratio;

  public PurePursuit(Optional<Tensor> lookAhead) {
    this.lookAhead = lookAhead;
    ratio = lookAhead.isPresent() //
        ? ratioPositiveX(lookAhead.orElseThrow())
        : Optional.empty();
  }

  /** @return */
  public Optional<Tensor> lookAhead() {
    return lookAhead;
  }

  @Override // from PursuitInterface
  public Optional<Scalar> firstRatio() {
    return ratio;
  }

  @Override // from PursuitInterface
  public Tensor ratios() {
    return Tensors.of(firstRatio().orElseThrow());
  }
}
