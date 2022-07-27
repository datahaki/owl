// code by jph
package ch.alpine.owl.bot.tse2;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.num.Roots;
import ch.alpine.tensor.sca.Sign;

/** use in combination with {@link Tse2Integrator} */
/* package */ class LinearVelocity2MinTime {
  private final Scalar v_max;
  private final Scalar a_max;

  /** @param v_max positive
   * @param a_max positive */
  public LinearVelocity2MinTime(Scalar v_max, Scalar a_max) {
    this.v_max = Sign.requirePositive(v_max);
    this.a_max = Sign.requirePositive(a_max);
  }

  /** @param d_tar
   * @param v_cur in [0, v_max]
   * @return
   * @throws Exception if v_cur is outside valid range */
  public Scalar minTime(final Scalar d_tar, final Scalar v_cur) {
    final TimeDistPair minDistToV_max = timeDistToV_max(v_cur);
    final Scalar d_remain = d_tar.subtract(minDistToV_max.dist);
    if (Sign.isPositive(d_remain)) {
      Scalar t_remain = d_remain.divide(v_max); // full speed
      return minDistToV_max.time.add(t_remain);
    }
    Tensor coeffs = Tensors.of(d_tar.negate(), v_cur, a_max.divide(RealScalar.of(2)));
    Scalar time = Roots.of(coeffs).Get(1);
    if (Sign.isPositive(Roots.of(coeffs).Get(0)))
      throw new Throw(coeffs);
    return Sign.requirePositiveOrZero(time);
  }

  /** @param v_cur less equals v_max
   * @return minimum distance required to achieve v_max regardless of integration method */
  public TimeDistPair timeDistToV_max(Scalar v_cur) {
    Scalar v_dif = Sign.requirePositiveOrZero(v_max.subtract(v_cur));
    Scalar time = v_dif.divide(a_max); // exact time to v_max
    Scalar dist = v_dif.multiply(v_max.add(v_cur)).divide(RealScalar.of(2).multiply(a_max));
    return new TimeDistPair(time, dist);
  }
}
