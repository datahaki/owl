// code by jph, gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.crv.dubins.DubinsPath;
import ch.alpine.sophus.crv.dubins.DubinsPathComparators;
import ch.alpine.sophus.crv.dubins.FixedRadiusDubins;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

public class DubinsTransitionSpace implements TransitionSpace, Serializable {
  /** @param radius positive
   * @param comparator
   * @return
   * @see DubinsPathComparators */
  public static TransitionSpace of(Scalar radius, Comparator<DubinsPath> comparator) {
    return new DubinsTransitionSpace( //
        Sign.requirePositive(radius), //
        Objects.requireNonNull(comparator));
  }

  // ---
  private final Scalar radius;
  private final Comparator<DubinsPath> comparator;

  private DubinsTransitionSpace(Scalar radius, Comparator<DubinsPath> comparator) {
    this.radius = radius;
    this.comparator = comparator;
  }

  @Override // from TransitionSpace
  public DubinsTransition connect(Tensor start, Tensor end) {
    return new DubinsTransition(start, end, dubinsPath(start, end));
  }

  private DubinsPath dubinsPath(Tensor start, Tensor end) {
    return FixedRadiusDubins.of(start, end, radius).stream().min(comparator).get();
  }
}
