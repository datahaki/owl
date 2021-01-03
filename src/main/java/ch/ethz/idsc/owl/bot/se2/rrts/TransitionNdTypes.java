// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparators;
import ch.ethz.idsc.sophus.gds.GeodesicDisplay;
import ch.ethz.idsc.sophus.gds.RnGeodesicDisplay;
import ch.ethz.idsc.sophus.gds.Se2AbstractGeodesicDisplay;
import ch.ethz.idsc.sophus.gds.Se2ClothoidDisplay;
import ch.ethz.idsc.tensor.RealScalar;

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

  static TransitionNdTypes fromString(GeodesicDisplay geodesicDisplay) {
    if (geodesicDisplay.equals(Se2ClothoidDisplay.ANALYTIC))
      return CLOTHOID_ANALYTIC;
    if (geodesicDisplay.equals(Se2ClothoidDisplay.LEGENDRE))
      return CLOTHOID_LEGENDRE;
    if (geodesicDisplay instanceof Se2AbstractGeodesicDisplay)
      return DUBINS;
    if (geodesicDisplay instanceof RnGeodesicDisplay)
      return RN;
    throw new IllegalArgumentException();
  }
}