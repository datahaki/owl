// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.bot.rn.RnTransitionSpace;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.crv.dubins.DubinsPathComparators;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.RnDisplay;
import ch.alpine.sophus.gds.Se2AbstractDisplay;
import ch.alpine.sophus.gds.Se2ClothoidDisplay;
import ch.alpine.tensor.RealScalar;

/* package */ enum TransitionNdTypes {
  /** clothoid curves */
  CLOTHOID_ANALYTIC(ClothoidTransitionSpace.ANALYTIC),
  /** clothoid curves */
  CLOTHOID_LEGENDRE(ClothoidTransitionSpace.LEGENDRE),
  /** dubins paths */
  // TODO magic const
  DUBINS(DubinsTransitionSpace.of(RealScalar.of(0.4), DubinsPathComparators.LENGTH)),
  /** straight lines in R^2 that results from ignoring heading */
  RN(RnTransitionSpace.INSTANCE), //
  ;

  private final TransitionSpace transitionSpace;

  private TransitionNdTypes(TransitionSpace transitionSpace) {
    this.transitionSpace = transitionSpace;
  }

  TransitionSpace transitionSpace() {
    return transitionSpace;
  }

  static TransitionNdTypes fromString(ManifoldDisplay geodesicDisplay) {
    if (geodesicDisplay.equals(Se2ClothoidDisplay.ANALYTIC))
      return CLOTHOID_ANALYTIC;
    if (geodesicDisplay.equals(Se2ClothoidDisplay.LEGENDRE))
      return CLOTHOID_LEGENDRE;
    if (geodesicDisplay instanceof Se2AbstractDisplay)
      return DUBINS;
    if (geodesicDisplay instanceof RnDisplay)
      return RN;
    throw new IllegalArgumentException();
  }
}